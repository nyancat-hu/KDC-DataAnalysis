package com.mcla.realtime.operators.func;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import com.alibaba.fastjson.JSON;
import com.mcla.realtime.bean.MCResource;
import com.mcla.realtime.operators.DPCOperator;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

public class KafkaProcessWindowFunction extends ProcessWindowFunction<MCResource, String, String, GlobalWindow> {
    @Override
    public void process(String type, ProcessWindowFunction<MCResource, String, String, GlobalWindow>.Context context, Iterable<MCResource> iterable, Collector<String> collector) throws Exception {
        // 统计数据的数量
        int count = 0;
        // 增强for循环遍历全窗口所有数据
        for (MCResource mcResource : iterable) {
            count += mcResource.getAlpha();
        }
        // 初始化一个二维数组来记录这个窗口里面的所有数据
        double data[][] = new double[count][];
        // 增强for循环遍历全窗口所有数据输入到二维数组
        int i = 0; // 计数
        for (MCResource mcResource : iterable) {
            while (mcResource.getAlpha() > 0) {
                data[i] = new double[]{mcResource.getX(), mcResource.getY()};
                mcResource.setAlpha(mcResource.getAlpha() - 1);
                i++;
            }
        }
        //调用 djl中的 NDArray数组 将data导入进行算法分析
        NDManager manager = NDManager.newBaseManager();
        NDArray nd = manager.create(data); // 创建NDArray数组

        DPCOperator dpcOperator = new DPCOperator();
        long[] centers = dpcOperator.DPC(nd);     //进行dp算法找出聚类中心点的下标
//        String resultStr = "id:" + integer + "聚类中心为：" + "x=" + data[Math.toIntExact(centersmax)][0] + "y=" + data[Math.toIntExact(centersmax)][1];
//        StringBuilder centersb = new StringBuilder();
        List<MCResource> mcResourceList = new ArrayList<MCResource>();
        for (long center : centers) {
//            mcResourceList.add(new MCResource(data[Math.toIntExact(center)][0], data[Math.toIntExact(center)][1], type, (int) (center + 1)));
            collector.collect(JSON.toJSONString(new MCResource(data[Math.toIntExact(center)][0], data[Math.toIntExact(center)][1], type, (int) (center + 1))));
//            centersb.append("(x: " + data[Math.toIntExact(center)][0] + ",y: " + data[Math.toIntExact(center)][1] + ",alpha: " + center + "),");
        }
//        String resultStr = "type:" + type + " 聚类中心为：" + centersb.deleteCharAt(centersb.length() - 1);
//        String resultStr = JSON.toJSONString(mcResourceList);
//        for (MCResource mcCenter : mcResourceList) {
//            collector.collect(JSON.toJSONString(mcCenter));
//        }
    }
}
