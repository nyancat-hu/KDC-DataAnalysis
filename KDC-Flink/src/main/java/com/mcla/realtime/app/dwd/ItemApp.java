package com.mcla.realtime.app.dwd;

import com.alibaba.fastjson.JSON;
import com.mcla.realtime.bean.DbscanBean;
import com.mcla.realtime.bean.ItemBean;
import com.mcla.realtime.operator.DBscanWindowProcessor;
import com.mcla.realtime.utils.MyKafkaUtil;
import com.mcla.realtime.utils.MyPravegaUtil;
import io.pravega.connectors.flink.FlinkPravegaReader;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.*;

/**
 * @Description: 统计物品相关值的Flink程序
 * @ClassName: ItemApp
 * @Author: ice_light
 * @Date: 2022/11/15 19:35
 * @Version: 1.0
 */
public class ItemApp {
    /*
     *  本程序用来统计以下几个跟物品有关的指标
     *  1.统计某种物品当前时间的数量
     *  2.输出物品的当前坐标，如果物品被销毁，同样输出当前坐标
     *  3.统计物品密集的中心坐标点，输出该坐标点属于哪个chunk
     * @author jhu
     * @date 2022/11/15 20:07
     */
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //TODO 1.从Pravega中读取数据
        String scope = "dwd_item_log";
        String stream = "base_item_app_stream";

        // 调用Pravega工具类，获取FlinkPravegaReader
        FlinkPravegaReader<String> pravegaSource = MyPravegaUtil.getPravegaReader(args, scope, stream);
        // 创建一个Pravega输入流
        DataStreamSource<String> jsonStrDS = env.addSource(pravegaSource);

        //TODO 2.将Item数据转为JavaBean
        SingleOutputStreamOperator<ItemBean> itemBeanDS = jsonStrDS.map((jsonStr) -> JSON.parseObject(jsonStr, ItemBean.class));

        //TODO 3.按物品名分类，统计每类掉落物品当前的数量
        KeyedStream<ItemBean, String> itemBeanKeyedDS = itemBeanDS.keyBy(ItemBean::getItemName);

        //TODO 4.定义一个测输出流输出实体存活
        OutputTag<Map<String,ArrayList<String>>> tag = new OutputTag<Map<String,ArrayList<String>>>("alive"){};

        SingleOutputStreamOperator<Tuple2<String, Long>> tuple2itemBeanKeyedDS = itemBeanKeyedDS.process(new KeyedProcessFunction<String, ItemBean, Tuple2<String, Long>>() {
            //定义状态，保存上次凋落物总量
            private ValueState<Long> lastItemNums;
            //定义状态，保存当前存活物品数量
            private MapState<String, Tuple3<String, String, String>> aliveItemIndex;

            @Override
            public void open(Configuration parameters) throws Exception {
                // 由上下文获取状态初值
                lastItemNums = getRuntimeContext().getState(new ValueStateDescriptor<Long>("last-item", Long.class));
                aliveItemIndex = getRuntimeContext().getMapState(new MapStateDescriptor<String, Tuple3<String, String, String>>("alive-item", Types.STRING, Types.TUPLE(Types.STRING, Types.STRING, Types.STRING)));
            }

            @Override
            public void processElement(ItemBean value, Context context, Collector<Tuple2<String, Long>> collector) throws Exception {
                // 获取上次数值
                Long valueLast = lastItemNums.value();
                if (valueLast == null) valueLast = 0L;// 初始状态时没有值，赋初值为0

                HashMap<String, ArrayList<String>> index = new HashMap<>();
                ArrayList<String> list= new ArrayList<String>(); //用于返回状态之中的数据

                // 若物品被拾取，则数量-amount，掉落则+amount
                if (value.getIsSpawn().equals("true")) {
                    lastItemNums.update(valueLast + Long.parseLong(value.getItemAmount()));
                    aliveItemIndex.put(value.getTag(), Tuple3.of(value.getX(), value.getY(), value.getZ()));
                    Iterator iterator = aliveItemIndex.iterator();
                    while (iterator.hasNext()) {
                        list.add(iterator.next().toString().split("=")[1]);
                        index.put(value.getItemName().split("\\.")[2],list);
                    }
                    context.output(tag, index);
                } else {
                    if (valueLast != 0) {
                        lastItemNums.update(valueLast - Long.parseLong(value.getItemAmount()));

                    }
                    if(lastItemNums.value() == null){
                        lastItemNums.update(0L);
                    }
                    aliveItemIndex.remove(value.getTag());
                    Iterator iterator = aliveItemIndex.iterator();

                    while(iterator.hasNext()){
                        list.add(iterator.next().toString().split("=")[1]);
                        index.put(value.getItemName().split("\\.")[2],list);

                    }

                    context.output(tag, index);
                }
                collector.collect(new Tuple2<>(value.getItemName().split("\\.")[2], lastItemNums.value()));
            }

            @Override
            public void close() throws Exception {
                lastItemNums.clear();
                aliveItemIndex.clear();
            }
        });

        DataStream<Map<String,ArrayList<String>>> sideOutput = tuple2itemBeanKeyedDS.getSideOutput(tag);
        sideOutput.flatMap(new FlatMapFunction<Map<String, ArrayList<String>>, Tuple2<String,String>>() {
            @Override
            public void flatMap(Map<String, ArrayList<String>> tuple, Collector<Tuple2<String, String>> collector) throws Exception {
                for (String value : tuple.keySet()) {
                        collector.collect(Tuple2.of(value,tuple.get(value).toString()));
                }
            }
        })
                .returns(Types.TUPLE(Types.STRING,Types.STRING)).addSink(JdbcSink.sink(
                "replace into ItemAlive(AliveName,AliveLocation) values (?,?)",
                (statement, str) -> {
                    statement.setString(1, str.f0);
                    statement.setString(2,str.f1.substring(1,str.f1.toCharArray().length-1).replace("),",");"));
                },
                JdbcExecutionOptions.builder()
                        .withBatchSize(10)
                        .withBatchIntervalMs(200)
                        .withMaxRetries(5)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl("jdbc:mysql://113.101.28.133:60005/mc_streaming?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false")
                        .withDriverName("com.mysql.jdbc.Driver")
                        .withUsername("root")
                        .withPassword("430525")
                        .build()
                )
        );

        // 输出每类掉落物品当前的数量
        tuple2itemBeanKeyedDS.returns(Types.TUPLE(Types.STRING, Types.LONG)).addSink(JdbcSink.sink(
                "replace into NumsCount (Name,Nums,Type) values (?, ?, ?)",
                (statement, str) -> {
                    statement.setString(1, str.f0);
                    statement.setInt(2, Integer.parseInt(str.f1.toString()));
                    statement.setString(3, "Item");
                },
                JdbcExecutionOptions.builder()
                        .withBatchSize(10)
                        .withBatchIntervalMs(200)
                        .withMaxRetries(5)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl("jdbc:mysql://topview102:3306/mc_streaming?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false")
                        .withDriverName("com.mysql.jdbc.Driver")
                        .withUsername("root")
                        .withPassword("430525")
                        .build()



                )
        );
        //输出聚类中心点
        sideOutput.flatMap((FlatMapFunction<Map<String,ArrayList<String>>, DbscanBean>) (strings, collector) -> {
            Collection<ArrayList<String>> values = strings.values();
            for (ArrayList<String> value : values) {
                for (String tuple : value) {
                    String[] replace = tuple.replace("(", "").replace(")", "").split(",");
                    collector.collect(new DbscanBean(Double.parseDouble(replace[0]),Double.parseDouble(replace[1]),Double.parseDouble(replace[2]),0));
                }
            }
        }).returns(TypeInformation.of(DbscanBean.class))
                .keyBy(data -> "DontChange")
                .window(TumblingProcessingTimeWindows.of(Time.seconds(10L)))
                .process(new DBscanWindowProcessor())
                .filter(str->!str.equals(""))
                .addSink(JdbcSink.sink(
                        "UPDATE DensityTable SET CenterPosition = ?,ChunkLocation = ? WHERE `Name` = 'item' ",
                        (statement, str) -> {
                            statement.setString(1, str);
                            String[] split = str.split(";");
                            StringBuilder sb = new StringBuilder();
                            for (String s : split) {
                                String[] split1 = s.split(",");
                                sb.append((int)Math.floor(Double.parseDouble(split1[0]) / 16)).append(",").append((int)Math.floor(Double.parseDouble(split1[1]) / 16)).append(",Y;");
                            }
                            String substring = sb.substring(0, sb.length() - 1);
                            statement.setString(2, substring);
                        },
                        JdbcExecutionOptions.builder()
                                .withBatchSize(1)
                                .withBatchIntervalMs(200)
                                .withMaxRetries(5)
                                .build(),
                        new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                                .withUrl("jdbc:mysql://192.168.88.245:3306/mc_streaming?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false")
                                .withDriverName("com.mysql.jdbc.Driver")
                                .withUsername("root")
                                .withPassword("430525")
                                .build()
                        )
                );

        //TODO 4.输出物品的当前坐标，以及物品当前状态是被销毁还是被创建
        sideOutput.print();
        env.execute("Item Module");
    }
}
