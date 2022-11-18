package com.mcla.realtime.app;

import ai.djl.Device;
import ai.djl.util.cuda.CudaUtils;
import com.alibaba.fastjson.JSONObject;
import com.mcla.realtime.bean.MCResource;

import com.mcla.realtime.operators.func.KafkaProcessWindowFunction;
import com.mcla.realtime.utils.MyKafkaUtil;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.lang.management.MemoryUsage;
import java.util.Objects;

public class DPCKafkaApp {

    public static void main(String[] args) throws Exception {
        // 默认查看entity
        String topic = "dwd_entity_log";
        String groupId = "base_dwd_entity_group";
        String type = "entity";
        if (Objects.equals(args[0], "item")) {
            topic = "dwd_item_log";
            groupId = "base_dwd_item_group";
            type = "item";
        } else if (Objects.equals(args[0], "state")) {
            topic = "dwd_state_log";
            groupId = "base_dwd_state_group";
            type = "state";
        } else if (Objects.equals(args[0], "tileentity")) {
            topic = "dwd_tileentity_log";
            groupId = "base_dwd_tileentity_group";
            type = "tileentity";
        }
        Configuration conf = new Configuration();
        // 获取执行环境
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
        // 创建本地web ui界面
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        // 调用Kafka工具类，获取FlinkKafkaConsumer
        FlinkKafkaConsumer<String> kafkaSource = MyKafkaUtil.getKafkaSource(topic, groupId);
        // 创建一个Kafka输入流，并将其转换为JSON
        DataStreamSource<String> jsonStrDS = env.addSource(kafkaSource);
//        jsonStrDS.print();
        String finalType = type;
        SingleOutputStreamOperator<MCResource> mcResourceDS = jsonStrDS.map(new MapFunction<String, MCResource>() {
            @Override
            public MCResource map(String json) throws Exception {
//                String[] splitData = s.split(",");
//                System.out.println("---id is:"+splitData[0]+"---- timeStamp is:"+splitData[1]+"---data is:"+splitData[2]);
                // 转化请求的 json 数据
                JSONObject jsonObject = JSONObject.parseObject(json);
                // 获取 x
                double x = Double.parseDouble(jsonObject.getString("x"));
                // 获取 y
                double y = Double.parseDouble(jsonObject.getString("y"));
                // 获取 alpha
                if (jsonObject.getString("alpha") != null) {
                    int alpha = Integer.parseInt(jsonObject.getString("alpha"));
                    return new MCResource(x, y, finalType, alpha);
                }
                return new MCResource(x, y, finalType, 1);
            }
        });
//        DataStreamSource<MCResource> inputDataStream = env.addSource(mcResourceDS);
//        DataStreamSink<String> resultDataStream = mcResourceDS.keyBy(data -> data.type)
//                .window(TumblingProcessingTimeWindows.of(Time.seconds(1L)))
//                .process(new KafkaProcessWindowFunction())
//                .print();


        // 输出至Kafka的主题中
        FlinkKafkaProducer<String> kafkaSink = MyKafkaUtil.getKafkaSink(topic);
        mcResourceDS.keyBy(data -> data.type)
                .countWindow(100)
//                .window(TumblingProcessingTimeWindows.of(Time.seconds(1L)))
                .process(new KafkaProcessWindowFunction())
                .addSink(kafkaSink);
        env.execute();
    }
}
