package com.mcla.kdccollectorbukkit.utils;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * @Description: spark分析器的工具类
 * @ClassName: SparkUtil
 * @Author: ice_light
 * @Date: 2022/11/14 12:25
 * @Version: 1.0
 */
public class SparkUtil {
    public static Spark getSpark(){
        return SparkProvider.get();
    }
}
