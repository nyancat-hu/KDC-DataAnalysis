package com.mcla.realtime.operators;

import com.mcla.realtime.bean.ClusterReading;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Random;

public class CustomSource {
    public static void main(String [] args) throws Exception {
        // 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 获取Source数据源
        DataStreamSource<ClusterReading> inputDataStream = env.addSource(new DPCCustomSource());
        // 打印输出
        inputDataStream.print();
        //执行
        env.execute();


    }

    public static class DPCCustomSource implements SourceFunction<ClusterReading> {
        // 定义无线循环，不断产生数据，除非被cancel
        boolean running = true;
        @Override
        public void run(SourceContext<ClusterReading> sourceContext) throws Exception {
            Random random = new Random();
            while (running)
            {
                int a = random.nextInt(3)+1;   //
                for(int i = 0;i<5;i++){
                    if(a == 1)
                    {
                        double x = Math.round((10.00 + random.nextDouble() * 20.00)*100)/100.0; // 范围[10,30]
                        double y = Math.round((10.00 + random.nextDouble() * 20.00)*100)/100.0;// 范围[10,30]
                        sourceContext.collect(new ClusterReading(x,y,a));
                        Thread.sleep(100L);
                    }
                    if(a == 2)
                    {
                        double x = Math.round((3.00 + random.nextDouble() * 25.00)*100)/100.0;  // 范围[3,28]
                        double y = Math.round((3.00 + random.nextDouble() * 25.00)*100)/100.0;  // 范围[3,28]
                        sourceContext.collect(new ClusterReading(x,y,a));
                        Thread.sleep(100L);
                    }
                    if(a == 3)
                    {
                        double x = Math.round((10.00 + random.nextDouble() * 20.00)*100)/100.0;  // 范围[10,30]
                        double y = Math.round((3.00 + random.nextDouble() * 25.00)*100)/100.0;  // 范围[3,28]
                        sourceContext.collect(new ClusterReading(x,y,a));
                        Thread.sleep(100L);
                    }
                }
                Thread.sleep(1000L);

            }

        }

        @Override
        public void cancel() {
            running = false;
        }
    }
}
