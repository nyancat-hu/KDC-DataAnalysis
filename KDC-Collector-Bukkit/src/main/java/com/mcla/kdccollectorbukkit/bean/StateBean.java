package com.mcla.kdccollectorbukkit.bean;

import com.bergerkiller.bukkit.common.server.SpigotServer;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import com.mcla.kdccollectorbukkit.utils.SparkUtil;
import me.lucko.spark.api.gc.GarbageCollector;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import me.lucko.spark.common.monitor.cpu.CpuMonitor;
import me.lucko.spark.common.monitor.tick.TickStatistics;
import org.bukkit.Bukkit;
import org.bukkit.Server;

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

    public double getTpsLast5Secs() {
        return tpsLast5Secs;
    }

    public void setTpsLast5Secs(double tpsLast5Secs) {
        this.tpsLast5Secs = tpsLast5Secs;
    }

    double tpsLast5Secs;


    double usagelastMin;
    Map<String, Map<Long,Double>> gc;

    public StateBean() {
        gc = new HashMap<>();
        Map<String, GarbageCollector> gcSpark = SparkUtil.getSpark().gc();

        setTpsLast5Secs(new TickStatistics().tps5Sec());
        setTpsLast10Secs(new TickStatistics().tps10Sec());

        setUsagelastMin(CpuMonitor.processLoad1MinAvg());
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
