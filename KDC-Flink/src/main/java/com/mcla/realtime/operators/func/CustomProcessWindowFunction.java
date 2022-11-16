package com.mcla.realtime.operators.func;


import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import com.mcla.realtime.bean.ClusterReading;
import com.mcla.realtime.operators.DPCOperator;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;


public class CustomProcessWindowFunction extends ProcessWindowFunction<ClusterReading, String, Integer, TimeWindow> {

    @Override
    public void process(Integer integer, ProcessWindowFunction<ClusterReading, String, Integer, TimeWindow>.Context context, Iterable<ClusterReading> iterable, Collector<String> collector) throws Exception {
        // 统计数据的数量
        int count = 0;
        //增强for循环遍历全窗口所有数据
        for (ClusterReading cluster : iterable) {
            count++;
        }
        //初始化一个二维数组来记录这个窗口里面的所有数据
        double data[][] = new double[count][];
        //增强for循环遍历全窗口所有数据输入到二维数组
        int i = 0; // 计数
        for (ClusterReading cluster : iterable) {
            data[i] = new double[]{cluster.getX(), cluster.getY()};
            i++;
        }
        //调用 djl中的 NDArray数组 将data导入进行算法分析
        NDManager manager = NDManager.newBaseManager();
        NDArray nd = manager.create(data); // 创建NDArray数组

        DPCOperator dpcOperator = new DPCOperator();
        long centersmax = dpcOperator.DPC(nd);     //进行dp算法找出聚类中心点的下标
        String resultStr = "id:" + integer + "聚类中心为：" + "x=" + data[Math.toIntExact(centersmax)][0] + "y=" + data[Math.toIntExact(centersmax)][1];
        collector.collect(resultStr);
    }
}

