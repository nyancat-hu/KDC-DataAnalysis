package com.mcla.kdccollectorbukkit.listeners;

import com.bergerkiller.bukkit.common.events.EntityAddEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveFromServerEvent;
import com.mcla.kdccollectorbukkit.KDCCollectorBukkit;
import com.mcla.kdccollectorbukkit.bean.*;
import com.mcla.kdccollectorbukkit.utils.HttpUtil;
import com.mcla.kdccollectorbukkit.utils.JsonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;


/**
 * @Description: 跟实体有关的事件监听器
 * @ClassName: MobListener
 * @Author: ice_light
 * @Date: 2022/10/12 21:33
 * @Version: 1.0
 */
public class MobListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void creatureSpawn(EntityAddEvent event) {
        if(!(event.getEntity() instanceof Player) && !(event.getEntity() instanceof Item)){
            MobSpawnBean mb = new MobSpawnBean(true);
            mb.setEssentialInfo(event);
            if(event.getEntity() instanceof LivingEntity) mb.setHealth((int) ((LivingEntity)event.getEntity()).getHealth());

            HttpUtil.postJson(KDCCollectorBukkit.targetUrl, JsonUtil.praseJson(mb));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void creatureDeath(EntityDeathEvent event) {
        MobDeathBean mb = new MobDeathBean(false);
        mb.setEssentialInfo(event);
        mb.setDroppedExp(event.getDroppedExp());
        mb.setHealth((int) event.getEntity().getHealth());
        HttpUtil.postJson(KDCCollectorBukkit.targetUrl,JsonUtil.praseJson(mb));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void creatureExplode(EntityExplodeEvent event) {
        MobExplodeBean mb = new MobExplodeBean(false);
        mb.setEssentialInfo(event);
        mb.setYield(event.getYield());
        HttpUtil.postJson(KDCCollectorBukkit.targetUrl,JsonUtil.praseJson(mb));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void creatureRemove(EntityRemoveFromServerEvent event) {
        if(!(event.getEntity() instanceof Player) && !(event.getEntity() instanceof Item)){
            MobRemoveBean mb = new MobRemoveBean(false);
            mb.setEssentialInfo(event);
            HttpUtil.postJson(KDCCollectorBukkit.targetUrl,JsonUtil.praseJson(mb));
        }
    }
}
