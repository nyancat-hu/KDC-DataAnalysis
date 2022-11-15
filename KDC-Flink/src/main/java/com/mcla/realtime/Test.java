package com.mcla.realtime;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class Test {
    public static void main(String[] args) throws Exception {
        // 获取Flink流执行环境
        StreamExecutionEnvironment streamExecutionEnvironment =
                StreamExecutionEnvironment.getExecutionEnvironment();
        // 获取socket输入数据
        DataStreamSource<String> textStream =
                streamExecutionEnvironment.socketTextStream("0.0.0.0", 7777, "\n");
        SingleOutputStreamOperator<Tuple2<String, Long>>
                sum = textStream.flatMap(new FlatMapFunction<String, Tuple2<String, Long>>() {
            @Override
            public void flatMap(String s, Collector<Tuple2<String, Long>>
                    collector) throws Exception {
                String[] splits = s.split("\\s");
                for (String word : splits) {
                    collector.collect(Tuple2.of(word, 1L));
                }
            }
        }).keyBy(0).sum(1);
        // 打印数据
        sum.print();
        // 触发任务执行
        streamExecutionEnvironment.execute("wordcount stream process");
    }

}
