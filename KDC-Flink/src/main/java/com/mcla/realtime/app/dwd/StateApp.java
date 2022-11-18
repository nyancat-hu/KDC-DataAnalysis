package com.mcla.realtime.app.dwd;

import com.alibaba.fastjson.JSON;
import com.mcla.realtime.bean.state.StateBean;
import com.mcla.realtime.utils.MyKafkaUtil;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Description:
 * @ClassName: StateApp
 * @Author: ice_light
 * @Date: 2022/11/16 14:02
 * @Version: 1.0
 */
public class StateApp {
    public static void main(String[] args) throws Exception {
        //TODO 1.从Kafka中读取数据
        String Topic = "dwd_state_log";
        String groupId = "base_state_app_group";

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 调用Kafka工具类，获取FlinkKafkaConsumer
        FlinkKafkaConsumer<String> kafkaSource = MyKafkaUtil.getKafkaSource(Topic, groupId);
        // 创建一个Kafka输入流
        DataStreamSource<String> jsonStrDS = env.addSource(kafkaSource);
        //TODO 2.将Item数据转为JavaBean
        SingleOutputStreamOperator<StateBean> stateBeanDS = jsonStrDS.map((jsonStr) -> JSON.parseObject(jsonStr, StateBean.class));

        SingleOutputStreamOperator<StateBean> timeDS = stateBeanDS.map(state -> {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            LocalDateTime now = LocalDateTime.now();
            state.setTime(now.format(dateTimeFormatter));
            return state;
        });

        timeDS.print();
        // 按时间戳写入当前状态到数据库


        //TODO 4.输出物品的当前坐标，以及物品当前状态是被销毁还是被创建

        env.execute("State Module");
    }
}
