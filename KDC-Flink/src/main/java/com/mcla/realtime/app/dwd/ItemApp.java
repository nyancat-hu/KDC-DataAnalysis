package com.mcla.realtime.app.dwd;

import com.alibaba.fastjson.JSON;
import com.mcla.realtime.bean.ItemBean;
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
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

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
     *  3.统计物品密集的中心坐标点，输出该物品属于哪个chunk
     * @author jhu
     * @date 2022/11/15 20:07
     */
    public static void main(String[] args) throws Exception {
        //TODO 1.从Kafka中读取数据
        String topic = "dwd_item_log";
        String groupId = "base_item_app_group";


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 调用Kafka工具类，获取FlinkKafkaConsumer
        FlinkKafkaConsumer<String> kafkaSource = MyKafkaUtil.getKafkaSource(topic, groupId);
        // 创建一个Kafka输入流
        DataStreamSource<String> jsonStrDS = env.addSource(kafkaSource);

        //TODO 2.将Item数据转为JavaBean
        SingleOutputStreamOperator<ItemBean> itemBeanDS = jsonStrDS.map((jsonStr) -> JSON.parseObject(jsonStr, ItemBean.class));

        //TODO 3.按物品名分类，统计每类掉落物品当前的数量
        KeyedStream<ItemBean, String> itemBeanKeyedDS = itemBeanDS.keyBy(ItemBean::getItemName);

        SingleOutputStreamOperator<Tuple2<String, Long>> tuple2itemBeanKeyedDS = itemBeanKeyedDS.flatMap(
                // 定义匿名Rich算子内部类，用以访问上下文保存状态
                new RichFlatMapFunction<ItemBean, Tuple2<String, Long>>() {
                    //定义状态，保存上次凋落物总量
                    private ValueState<Long> lastItemNums;

                    @Override
                    public void open(Configuration parameters) {
                        // 由上下文获取状态初值
                        lastItemNums = getRuntimeContext().getState(new ValueStateDescriptor<Long>("last-item", Long.class));

                    }

                    @Override
                    public void flatMap(ItemBean value, Collector<Tuple2<String, Long>> out) throws Exception {
                        // 获取上次数值
                        Long valueLast = lastItemNums.value();
                        if(valueLast==null) valueLast = 0L;// 初始状态时没有值，赋初值为0
                        // 若物品被拾取，则数量-amount，掉落则+amount
                        if (value.getIsSpawn().equals("true")) {
                            lastItemNums.update(valueLast + Long.parseLong(value.getItemAmount()));
                        } else {
                            lastItemNums.update(valueLast - Long.parseLong(value.getItemAmount()));
                        }
                        out.collect(new Tuple2<>(value.getItemName(), lastItemNums.value()));
                    }

                    @Override
                    public void close() throws Exception {
                        lastItemNums.clear();
                    }
                }
        );
        // 输出每类掉落物品当前的数量
        tuple2itemBeanKeyedDS.print();

        //TODO 4.输出物品的当前坐标，以及物品当前状态是被销毁还是被创建



        env.execute("Item Module");
    }
}
