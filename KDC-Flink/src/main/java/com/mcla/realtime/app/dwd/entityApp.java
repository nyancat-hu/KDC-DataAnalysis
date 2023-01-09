package com.mcla.realtime.app.dwd;

import com.alibaba.fastjson.JSON;
import com.mcla.realtime.bean.DbscanBean;
import com.mcla.realtime.bean.EntityBean;
import com.mcla.realtime.operator.DBscanWindowProcessor;
import com.mcla.realtime.utils.MyPravegaUtil;
import io.pravega.connectors.flink.FlinkPravegaReader;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
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
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.*;

public class entityApp {
    /*
     *  本程序用来统计以下几个跟物品有关的指标
     *  1.统计某种物品当前时间的数量
     *  2.输出物品的当前坐标，如果物品被销毁，同样输出当前坐标
     *  3.统计物品密集的中心坐标点，输出该物品属于哪个chunk
     * @author jhu
     * @date 2022/11/15 20:07
     */
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //TODO 1.从Pravega中读取数据
        String scope = "dwd_entity_log";
        String stream = "base_entity_app_stream";

        // 调用Pravega工具类，获取FlinkPravegaReader
        FlinkPravegaReader<String> pravegaSource = MyPravegaUtil.getPravegaReader(args, scope, stream);
        // 创建一个Pravega输入流
        DataStreamSource<String> jsonStrDS = env.addSource(pravegaSource);
        //TODO 2.将Item数据转为JavaBean
        SingleOutputStreamOperator<EntityBean> EntityBeanDS = jsonStrDS.map((jsonStr) -> JSON.parseObject(jsonStr, EntityBean.class));
        //TODO 3.按物品名分类，统计每类掉落物品当前的数量
        KeyedStream<EntityBean, String> EntityBeankeyDS = EntityBeanDS.keyBy(EntityBean::getEntityName);
        //TODO 4.定义一个测输出流输出生物实体存活
        OutputTag<Map<String,ArrayList<String>>> tag = new OutputTag<Map<String,ArrayList<String>>>("alive") {
        };

        SingleOutputStreamOperator<Tuple2<String, Long>> tuple2EntityBeanKeyedDS = EntityBeankeyDS.process(new KeyedProcessFunction<String, EntityBean, Tuple2<String, Long>>() {
            //定义状态，保存上次凋落物总量
            private ValueState<Long> lastEntityNums;
            //定义状态，记录存活生物的tag和坐标
            private MapState<String, Tuple3<String, String, String>> alive;

            @Override
            public void open(Configuration parameters) throws Exception {
                //获取上下文参数
                lastEntityNums = getRuntimeContext().getState(new ValueStateDescriptor<Long>("last-item", Long.class));
                alive = getRuntimeContext().getMapState(new MapStateDescriptor<>("Entity-alive", Types.STRING, Types.TUPLE(Types.STRING, Types.STRING, Types.STRING)));
            }

            @Override
            public void processElement(EntityBean value, Context context, Collector<Tuple2<String, Long>> collector) throws Exception {
                // 获取上次数值
                Long valueLast = lastEntityNums.value();
                HashMap<String, ArrayList<String>> index = new HashMap<>();
                ArrayList<String> list = new ArrayList<>();
                if (valueLast == null) {valueLast = 0L;}// 初始状态时没有值，赋初值为0
                // 若实体被杀死，则数量-amount，掉落则+amount
                if (value.getIsSpawn().equals("true")) {
                    lastEntityNums.update(valueLast + 1L);
                    alive.put(value.getTag(), Tuple3.of(value.getX(), value.getY(), value.getZ()));
                    Iterator<Map.Entry<String, Tuple3<String, String, String>>> iterator = alive.iterator();
                    while (iterator.hasNext()) {
                        list.add(iterator.next().toString().split("=")[1]);
                        index.put(value.getEntityName(),list);
                    }
                    context.output(tag, index);

                } else {
                    if (valueLast != 0 ) {
                        lastEntityNums.update(valueLast - 1L);
                    }
                    if(lastEntityNums.value() == null){
                        lastEntityNums.update(0L);
                    }
                    alive.remove(value.getTag());

                    Iterator iterator = alive.iterator();

                    while (iterator.hasNext()) {
                        list.add(iterator.next().toString().split("=")[1]);
                        index.put(value.getEntityName(),list);
                    }
                    context.output(tag, index);
                }
                collector.collect(new Tuple2<>(value.getEntityName(), lastEntityNums.value()));
            }

            @Override
            public void close() throws Exception {
                lastEntityNums.clear();
                alive.clear();
            }
        });

        // 输出每类掉落物品当前的数量
        tuple2EntityBeanKeyedDS.returns(Types.TUPLE(Types.STRING, Types.LONG)).addSink(
                JdbcSink.sink(
                        "replace into NumsCount(Name,Nums,Type) values (?, ? ,?)",
                        (statement, str) -> {
                            statement.setString(1, str.f0);
                            statement.setString(2, str.f1.toString());
                            statement.setString(3,"Entity");
                        },
                        new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                                .withUrl("jdbc:mysql://113.101.28.133:60005/mc_streaming?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false")

                                .withDriverName("com.mysql.jdbc.Driver")
                                .withUsername("root")
                                .withPassword("430525")
                                .build()
                )
        );

        //输出当前存活生物的下标
        DataStream<Map<String,ArrayList<String>>> sideOutput = tuple2EntityBeanKeyedDS.getSideOutput(tag);
        sideOutput.flatMap(
                new FlatMapFunction<Map<String, ArrayList<String>>, Tuple2<String,String>>() {
                    @Override
                    public void flatMap(Map<String, ArrayList<String>> tuple, Collector<Tuple2<String, String>> collector) throws Exception {
                        for (String value : tuple.keySet()) {
                                collector.collect(Tuple2.of(value,tuple.get(value).toString()));
                        }
                    }
                }
        )
                .returns(Types.TUPLE(Types.STRING, Types.STRING)).addSink(JdbcSink.sink(
                        "replace into EntityAlive (AliveHashCode,AliveLocation) values (?, ?)",
                        (statement, str) -> {
                            statement.setString(1,  String.format("%d", str.f0.hashCode()));
                            statement.setString(2, str.f1.substring(1,str.f1.toCharArray().length-1).replace("),",");"));
                        },
                        JdbcExecutionOptions.builder()
                                .withBatchSize(10)
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
        //输出当前存活的生物
//        sideOutput.print();

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
                        "UPDATE DensityTable SET CenterPosition = ?,ChunkLocation = ? WHERE `Name` = 'entity' ",
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

        env.execute("Entity Module");
    }
}
