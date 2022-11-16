package com.mcla.kdccollectorbukkit;

import com.mcla.kdccollectorbukkit.config.Config;
import com.mcla.kdccollectorbukkit.listeners.ItemListener;
import com.mcla.kdccollectorbukkit.listeners.MobListener;
import com.mcla.kdccollectorbukkit.listeners.StateListener;
import com.mcla.kdccollectorbukkit.listeners.TileListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class KDCCollectorBukkit extends JavaPlugin {
    public static KDCCollectorBukkit snowkPlugin;
    public static String targetUrl;

    @Override
    public void onEnable() {
        snowkPlugin = this;
        Config.loadConfig("config.yml");
        targetUrl = Config.getString("url");
        getLogger().info("[KDC-Collector-Bukkit]已启用 - By:ice_light");
        getLogger().info("源码于：https://github.com/nyancat-hu/KDC-DataAnalysis 敬请关注更新！");
        getServer().getPluginManager().registerEvents(new MobListener(), this);
        getServer().getPluginManager().registerEvents(new ItemListener(), this);
        getServer().getPluginManager().registerEvents(new TileListener(), this);
        getServer().getPluginManager().registerEvents(new StateListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("[KDC-Collector-Bukkit]已关闭");
    }
}
