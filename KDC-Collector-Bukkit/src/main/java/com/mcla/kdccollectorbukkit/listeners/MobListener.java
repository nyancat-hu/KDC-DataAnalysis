package com.mcla.kdccollectorbukkit.listeners;

import com.mcla.kdccollectorbukkit.bean.MobBean;
import com.mcla.kdccollectorbukkit.config.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import static com.mcla.kdccollectorbukkit.KDCCollectorBukkit.snowkPlugin;

/**
 * @Description: listen mob about events for minecraft  // 类说明，在创建类时要填写
 * @ClassName: MobListener    // 类名，会自动填充
 * @Author: ice_light          // 创建者
 * @Date: 2022/10/12 21:33   // 时间
 * @Version: 1.0     // 版本
 */
public class MobListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void creatureSpawn(CreatureSpawnEvent event) {
        MobBean mb = new MobBean();
        mb.setEquipment(event.getEntity().getEquipment());
        mb.setEntityName(event.getEntity().getName());
        mb.setEventName(event.getEventName());
        mb.setHealth((int) event.getEntity().getHealth());
        mb.setSpawnReason(event.getSpawnReason());
        mb.setLocation(event.getLocation());
        String url = Config.getString("url");
        System.out.println(mb.toString());
        System.out.println("日志已产生");
    }
}
