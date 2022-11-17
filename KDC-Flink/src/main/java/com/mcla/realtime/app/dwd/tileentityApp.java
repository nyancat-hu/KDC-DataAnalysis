package com.mcla.realtime.app.dwd;

import com.alibaba.fastjson.JSON;
import com.mcla.realtime.bean.EntityBean;
import com.mcla.realtime.bean.ItemBean;
import com.mcla.realtime.bean.TileEntityBean;
import com.mcla.realtime.utils.MyKafkaUtil;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

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

        SingleOutputStreamOperator<Tuple2<String, Long>>  tuple2tileEntityBeanKeyedDS = tileEntityBeankeyDS.process(new KeyedProcessFunction<String, TileEntityBean, Tuple2<String, Long>>() {
            //定义一个状态，记录tileEntity的值
            private ValueState<Long> lasttileEntityNums;
           @Override
            public void open(Configuration parameters) throws Exception {
            // 由上下文获取状态初值
            lasttileEntityNums = getRuntimeContext().getState(new ValueStateDescriptor<Long>("last-tileEntity", Long.class));
            }

            @Override
            public void processElement(TileEntityBean value, Context context, Collector<Tuple2<String, Long>> collector) throws Exception {
                // 获取上次数值
                Long valueLast = lasttileEntityNums.value();
                if (valueLast == null) valueLast = 0L;// 初始状态时没有值，赋初值为0
                // 若物品被拾取，则数量-amount，掉落则+amount
                if (value.getIsPlace().equals("true")) {
                    lasttileEntityNums.update(valueLast + 1L);
                } else {
                    lasttileEntityNums.update(valueLast - 1L);
                }
                collector.collect(new Tuple2<>(value.getBlockName(), lasttileEntityNums.value()));
            }

            @Override
            public void close() throws Exception {
               lasttileEntityNums.clear();
            }
        });

        // 输出每类掉落物品当前的数量
        tuple2tileEntityBeanKeyedDS.print();

        //TODO 4.输出物品的当前坐标，以及物品当前状态是被销毁还是被创建



        env.execute("tileEnity Module");

    }
}
