package com.mcla.kdccollectorbukkit.listeners;

import com.mcla.kdccollectorbukkit.KDCCollectorBukkit;
import com.mcla.kdccollectorbukkit.bean.StateBean;
import com.mcla.kdccollectorbukkit.utils.HttpUtil;
import com.mcla.kdccollectorbukkit.utils.JsonUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.logging.LogRecord;

/**
 * @Description: 监听服务器状态以发送至Web端的监听器
 * @ClassName: StateListener
 * @Author: ice_light
 * @Date: 2022/11/14 12:54
 * @Version: 1.0
 */
public class StateListener implements Listener {
    @EventHandler
    public void onPluginInit(PluginEnableEvent event) {
        // 创建一个20tick执行的匿名任务
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(KDCCollectorBukkit.snowkPlugin, () -> {
                // 执行定时任务
                StateBean spt = new StateBean();
                Bukkit.getServer().broadcast(JsonUtil.praseJson(spt),"*");
                HttpUtil.postJson(KDCCollectorBukkit.targetUrl, JsonUtil.praseJson(spt));
        }, 0L, 50L);
    }
}
