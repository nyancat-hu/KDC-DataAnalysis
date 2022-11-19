package com.mcla.realtime.app.dwd;

import com.alibaba.fastjson.JSON;
import com.mcla.realtime.bean.EntityBean;
import com.mcla.realtime.bean.ItemBean;
import com.mcla.realtime.bean.TileEntityBean;
import com.mcla.realtime.utils.MyKafkaUtil;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.ArrayList;
import java.util.Iterator;

public class tileentityApp {
    public static void main(String[] args) throws Exception {
        //TODO 1.从Kafka中读取数据
        String Topic = "dwd_tileentity_log";
        String groupId = "base_tileEntity_app_group";

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 调用Kafka工具类，获取FlinkKafkaConsumer
        FlinkKafkaConsumer<String> kafkaSource = MyKafkaUtil.getKafkaSource(Topic, groupId);
        // 创建一个Kafka输入流
        DataStreamSource<String> jsonStrDS = env.addSource(kafkaSource);
        //TODO 2.将Item数据转为JavaBean
        SingleOutputStreamOperator<TileEntityBean> EntityBeanDS = jsonStrDS.map((jsonStr) -> JSON.parseObject(jsonStr, TileEntityBean.class));
        //TODO 3.按物品名分类，统计每类掉落物品当前的数量
        KeyedStream<TileEntityBean, String> tileEntityBeankeyDS = EntityBeanDS.keyBy(TileEntityBean::getType);
        //TODO 4.定义一个测输出流输出生物实体存活
        OutputTag<ArrayList<String>> tag = new OutputTag<ArrayList<String>>("alive") {
        };

        SingleOutputStreamOperator<Tuple2<String, Long>> tuple2tileEntityBeanKeyedDS = tileEntityBeankeyDS.process(new KeyedProcessFunction<String, TileEntityBean, Tuple2<String, Long>>() {
            //定义一个状态，记录tileEntity的值
            private ValueState<Long> lasttileEntityNums;
            //定义一个状态，定义状态，记录存活生物的tag和坐标
            private MapState<String, Tuple3<String, String, String>> alive;

            @Override
            public void open(Configuration parameters) throws Exception {
                // 由上下文获取状态初值
                lasttileEntityNums = getRuntimeContext().getState(new ValueStateDescriptor<Long>("last-tileEntity", Long.class));
                alive = getRuntimeContext().getMapState(new MapStateDescriptor<String, Tuple3<String, String, String>>("Entity-alive", Types.STRING, Types.TUPLE(Types.STRING, Types.STRING, Types.STRING)));
            }

            @Override
            public void processElement(TileEntityBean value, Context context, Collector<Tuple2<String, Long>> collector) throws Exception {
                // 获取上次数值
                Long valueLast = lasttileEntityNums.value();
                ArrayList<String> index = new ArrayList<String>(); //用于返回状态之中的数据
                if (valueLast == null) valueLast = 0L;// 初始状态时没有值，赋初值为0
                // 若物品被拾取，则数量-amount，掉落则+amount
                if (value.getIsPlace().equals("true")) {
                    lasttileEntityNums.update(valueLast + 1L);
                    alive.put(value.getTag(), Tuple3.of(value.getX(), value.getY(), value.getZ()));
                    Iterator iterator = alive.iterator();
                    while (iterator.hasNext()) {
                        index.add(iterator.next().toString().split("=")[1]);
                    }
                    context.output(tag, index);
                } else {
                    if (valueLast != 0) {
                        lasttileEntityNums.update(valueLast - 1L);
                    }
                    alive.remove(value.getTag());
                    Iterator iterator = alive.iterator();
                    while (iterator.hasNext()) {
                        index.add(iterator.next().toString().split("=")[1]);
                    }
                    context.output(tag, index);
                }
                collector.collect(new Tuple2<>(value.getBlockName(), lasttileEntityNums.value()));
            }

            @Override
            public void close() throws Exception {
                lasttileEntityNums.clear();
            }
        });

        // 输出每类掉落物品当前的数量

        tuple2tileEntityBeanKeyedDS.addSink(
                JdbcSink.sink(
                        "insert into NumsCount (Name,Nums,Type) values (?,?,?)",
                        (statement, str) -> {
                            statement.setString(1, str.f0);
                            statement.setInt(2, Integer.parseInt(str.f1.toString()));
                            statement.setString(3, String.format("%d", str.hashCode()));
                        },
                        new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                                .withUrl("jdbc:mysql://topview102:3306/mc_streaming?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false")
                                .withDriverName("com.mysql.jdbc.Driver")
                                .withUsername("root")
                                .withPassword("430525")
                                .build()
                )
        );
        tuple2tileEntityBeanKeyedDS.print();


        //TODO 4.输出物品的当前坐标，以及物品当前状态是被销毁还是被创建
        //输出当前存活生物的下标
        DataStream<ArrayList<String>> sideOutput = tuple2tileEntityBeanKeyedDS.getSideOutput(tag);
        sideOutput.flatMap(new FlatMapFunction<ArrayList<String>, String>() {
            @Override
            public void flatMap(ArrayList<String> strings, Collector<String> collector) throws Exception {
                for (String tuple : strings) {
                    collector.collect(tuple);
                }
            }
        }).addSink(JdbcSink.sink(
                "insert into TileEntityAlive (AliveHashCode,AliveLocation) values (?,?)",
                (statement, str) -> {
                    statement.setString(1, String.format("%d", str.hashCode()));
                    statement.setString(2, str);
                },
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl("jdbc:mysql://topview102:3306/mc_streaming?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false")
                        .withDriverName("com.mysql.jdbc.Driver")
                        .withUsername("root")
                        .withPassword("430525")
                        .build()
                )
        );
        sideOutput.print();
        env.execute("tileEnity Module");

    }
}
