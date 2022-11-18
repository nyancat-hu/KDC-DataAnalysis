package com.mcla.realtime.operator;

import com.alibaba.fastjson.JSONObject;
import com.mcla.realtime.bean.DbscanBean;
import com.mcla.realtime.utils.MyKafkaUtil;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

/**
 * @Description: Kmeans的测试类
 * @ClassName: TestKmeans
 * @Author: ice_light
 * @Date: 2022/11/18 19:01
 * @Version: 1.0
 */
public class TestKmeans {
    public static void main(String[] args) throws Exception {
        String topic = "dwd_entity_log";
        String groupId = "base_dwd_entity_group";

        // 创建本地web ui界面
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        // 调用Kafka工具类，获取FlinkKafkaConsumer
        FlinkKafkaConsumer<String> kafkaSource = MyKafkaUtil.getKafkaSource(topic, groupId);
        // 创建一个Kafka输入流，并将其转换为JSON
        DataStreamSource<String> jsonStrDS = env.addSource(kafkaSource);

        SingleOutputStreamOperator<DbscanBean> mcResourceDS = jsonStrDS.map((MapFunction<String, DbscanBean>) json -> {
            // 转化请求的 json 数据
            JSONObject jsonObject = JSONObject.parseObject(json);
            // 获取 x
            double x = Double.parseDouble(jsonObject.getString("x"));
            // 获取 y
            double y = Double.parseDouble(jsonObject.getString("y"));
            double z = Double.parseDouble(jsonObject.getString("z"));
            // 获取 alpha
            if (jsonObject.getString("alpha") != null) {
                int alpha = Integer.parseInt(jsonObject.getString("alpha"));
                return new DbscanBean(x, y, z, alpha);
            }
            return new DbscanBean(x, y, z, 1);
        });

        mcResourceDS.keyBy(data -> "DontChange")
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5L)))
                .process(new DBscanWindowProcessor());

        env.execute();
    }
}
