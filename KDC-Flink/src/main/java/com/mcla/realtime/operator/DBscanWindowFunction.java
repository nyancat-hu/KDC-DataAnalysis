package com.mcla.realtime.operator;

import com.mcla.realtime.bean.DbscanBean;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.operators.IterativeDataSet;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @ClassName: DBscanWindowFunction
 * @Author: ice_light
 * @Date: 2022/11/18 19:02
 * @Version: 1.0
 */
public class DBscanWindowFunction extends ProcessWindowFunction<DbscanBean, String, String, TimeWindow> {
    @Override
    public void process(String type, ProcessWindowFunction<DbscanBean, String, String, TimeWindow>.Context context, Iterable<DbscanBean> iterable, Collector<String> collector) {
        List<DoublePoint> points = new ArrayList<>();
        for (DbscanBean mcResource : iterable) {
            points.add(new DoublePoint(new double[]{mcResource.getX(), mcResource.getY()}));
        }
        ArrayList<DoublePoint> result = new ArrayList<>();
        DBSCANClusterer dbscan = new DBSCANClusterer(1.2, 5);
        List<Cluster<DoublePoint>> cluster = dbscan.cluster(points);
        for (int i = 0; i < cluster.size(); i++) {
            result.add(cluster.get(i).getPoints().get(0));
            if(i>=3) break;
        }

//        DBScanOperator.trainAndPredict(mc);
        collector.collect(String.valueOf(result));
    }
}
