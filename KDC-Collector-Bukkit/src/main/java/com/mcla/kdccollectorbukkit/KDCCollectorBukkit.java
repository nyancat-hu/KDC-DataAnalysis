package com.mcla.kdccollectorbukkit;

import com.mcla.kdccollectorbukkit.config.Config;
import com.mcla.kdccollectorbukkit.listeners.MobListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class KDCCollectorBukkit extends JavaPlugin {
    public static KDCCollectorBukkit snowkPlugin;

    @Override
    public void onEnable() {
        snowkPlugin = this;
        Config.loadConfig("config.yml");
        getLogger().info("[KDC-Collector-Bukkit]已启用 - By:Bear");
        getLogger().info("源码于：https://github.com/nyancat-hu/KDC-DataAnalysis 敬请关注更新！");
        getServer().getPluginManager().registerEvents(new MobListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("[KDC-Collector-Bukkit]已关闭");
    }
}
