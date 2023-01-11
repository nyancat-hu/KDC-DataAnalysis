package com.mcla.realtime.operator;

import com.mcla.realtime.bean.DbscanBean;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
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
public class DBscanWindowProcessor extends ProcessWindowFunction<DbscanBean, String, String, TimeWindow> {

    public String sname;

    public DBscanWindowProcessor(String sname) {
        super();
        this.sname = sname;
    }

    private transient ListState<Tuple3<Double, Double, Integer>> centers;

    @Override
    public void process(String type, ProcessWindowFunction<DbscanBean, String, String, TimeWindow>.Context context,
                        Iterable<DbscanBean> iterable, Collector<String> collector) throws Exception {
        List<DoublePoint> npoints = new ArrayList<>();
        List<Tuple3<Double, Double, Integer>> cpoints = (List<Tuple3<Double, Double, Integer>>) centers.get();
        for (Tuple3<Double, Double, Integer> cpoint : cpoints) {
            while (cpoint.f2 > 1) {
                npoints.add(new DoublePoint(new double[]{cpoint.f0, cpoint.f1}));
                cpoint.setField(cpoint.f2 - 1, 2);
            }
        }
        for (DbscanBean mcResource : iterable) {
            npoints.add(new DoublePoint(new double[]{mcResource.getX(), mcResource.getZ()}));
        }
        StringBuilder result = new StringBuilder();
        cpoints = new ArrayList<>();
        DBSCANClusterer dbscan = new DBSCANClusterer(1.2, 5);
        List<Cluster<DoublePoint>> cluster = dbscan.cluster(npoints);

        for (Cluster<DoublePoint> doublePointCluster : cluster) {
            cpoints.add(new Tuple3<>(doublePointCluster.getPoints().get(0).getPoint()[0],
                    doublePointCluster.getPoints().get(0).getPoint()[1], doublePointCluster.getPoints().size()));
            result.append(doublePointCluster.getPoints().get(0)).append(";");
        }
        String replace = result.toString().replace("[", "").replace("]", "");
        String substring = "";
        if (replace.length() > 1) substring = replace.substring(0, replace.length() - 1);
        centers.update(cpoints);
        collector.collect(substring);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        ListStateDescriptor<Tuple3<Double, Double, Integer>> descriptor =
                new ListStateDescriptor<>(
                        sname,
                        Types.TUPLE(Types.DOUBLE, Types.DOUBLE, Types.INT)
                );
        centers = getRuntimeContext().getListState(descriptor);
    }
}
