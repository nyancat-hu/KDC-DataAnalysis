package com.mcla.kdccollectorbukkit.bean;

import com.mcla.kdccollectorbukkit.utils.SparkUtil;
import me.lucko.spark.api.gc.GarbageCollector;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import me.lucko.spark.api.statistic.types.GenericStatistic;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 有关服务器当前状态的Bean
 * @ClassName: StateBean
 * @Author: ice_light
 * @Date: 2022/11/14 12:01
 * @Version: 1.0
 */
public class StateBean {
    double tpsLast10Secs;
    double tpsLast5Mins;
    double msptMean;
    double mspt95Percentile;
    double usagelastMin;
    Map<String, Map<Long,Double>> gc;

    public StateBean() {
        gc = new HashMap<>();
        Map<String, GarbageCollector> gcSpark = SparkUtil.getSpark().gc();
        DoubleStatistic<StatisticWindow.CpuUsage> cpuUsage = SparkUtil.getSpark().cpuSystem();
        DoubleStatistic<StatisticWindow.TicksPerSecond> tps = SparkUtil.getSpark().tps();
        GenericStatistic<DoubleAverageInfo, StatisticWindow.MillisPerTick> mspt = SparkUtil.getSpark().mspt();
        setTpsLast5Mins(tps.poll(StatisticWindow.TicksPerSecond.SECONDS_10));
        setTpsLast10Secs(tps.poll(StatisticWindow.TicksPerSecond.MINUTES_5));
        setMsptMean(mspt.poll(StatisticWindow.MillisPerTick.MINUTES_1).mean());
        setMspt95Percentile(mspt.poll(StatisticWindow.MillisPerTick.MINUTES_1).percentile95th());
        setUsagelastMin(cpuUsage.poll(StatisticWindow.CpuUsage.MINUTES_1));
        for (GarbageCollector collector : gcSpark.values()) {
            Map<Long,Double> innerGC = new HashMap<>();
            innerGC.put(collector.avgFrequency(),collector.avgTime());
            gc.put(collector.name(),innerGC);
        }
    }

    public double getTpsLast10Secs() {
        return tpsLast10Secs;
    }

    public void setTpsLast10Secs(double tpsLast10Secs) {
        this.tpsLast10Secs = tpsLast10Secs;
    }

    public double getTpsLast5Mins() {
        return tpsLast5Mins;
    }

    public void setTpsLast5Mins(double tpsLast5Mins) {
        this.tpsLast5Mins = tpsLast5Mins;
    }

    public double getMsptMean() {
        return msptMean;
    }

    public void setMsptMean(double msptMean) {
        this.msptMean = msptMean;
    }

    public double getMspt95Percentile() {
        return mspt95Percentile;
    }

    public void setMspt95Percentile(double mspt95Percentile) {
        this.mspt95Percentile = mspt95Percentile;
    }

    public double getUsagelastMin() {
        return usagelastMin;
    }

    public void setUsagelastMin(double usagelastMin) {
        this.usagelastMin = usagelastMin;
    }

    public Map<String, Map<Long, Double>> getGc() {
        return gc;
    }

    public void setGc(Map<String, Map<Long, Double>> gc) {
        this.gc = gc;
    }
}
