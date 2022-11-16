package com.mcla.realtime.app;

import com.mcla.realtime.bean.ClusterReading;

import com.mcla.realtime.operators.CustomSource;
import com.mcla.realtime.operators.func.CustomProcessWindowFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

public class DPCTestApp {
    public static void main(String[] args) throws Exception {
        // 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 获取Source数据源
        DataStreamSource<ClusterReading> inputDataStream = env.addSource(new CustomSource.DPCCustomSource());
        DataStreamSink<String> resultDataStream = inputDataStream.keyBy(data -> data.id)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(10L)))
                .process(new CustomProcessWindowFunction())
                .print();
        env.execute();
    }

}
