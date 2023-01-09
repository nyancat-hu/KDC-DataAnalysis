package com.mcla.realtime.app.dwd;

import com.alibaba.fastjson.JSON;
import com.mcla.realtime.bean.state.StateBean;
import com.mcla.realtime.utils.MyPravegaUtil;
import io.pravega.connectors.flink.FlinkPravegaReader;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

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
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //TODO 1.从Pravega中读取数据
        String scope = "dwd_state_log";
        String stream = "base_state_app_stream";

        // 调用Pravega工具类，获取FlinkPravegaReader
        FlinkPravegaReader<String> pravegaSource = MyPravegaUtil.getPravegaReader(args, scope, stream);
        // 创建一个Pravega输入流
        DataStreamSource<String> jsonStrDS = env.addSource(pravegaSource);

        //TODO 2.将Item数据转为JavaBean
        SingleOutputStreamOperator<StateBean> stateBeanDS = jsonStrDS.map((jsonStr) -> JSON.parseObject(jsonStr, StateBean.class));

        SingleOutputStreamOperator<StateBean> timeDS = stateBeanDS.map(state -> {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            state.setTime(now.format(dateTimeFormatter));
            return state;
        });

        timeDS.addSink(JdbcSink.sink(
                "replace into StateTable (Time,StateJson) values (?, ?)",
                (statement, state) -> {
                    statement.setString(1, state.getTime());
                    statement.setString(2, state.toString());
                },
                JdbcExecutionOptions.builder()
                        .withBatchSize(1)
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

        env.execute("State Module");
    }
}
