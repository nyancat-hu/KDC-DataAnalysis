package com.mcla.kdccollectorbukkit.utils;

import me.lucko.spark.api.Spark;
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
    private static Spark spark;
    public static Spark getSpark(){
        RegisteredServiceProvider<Spark> provider = Bukkit.getServicesManager().getRegistration(Spark.class);
        if (provider != null&&spark==null) {
            spark = provider.getProvider();
        }
        return spark;
    }
}
