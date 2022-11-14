package com.mcla.kdccollectorbukkit.listeners;

import com.mcla.kdccollectorbukkit.KDCCollectorBukkit;
import com.mcla.kdccollectorbukkit.bean.ItemDropBean;
import com.mcla.kdccollectorbukkit.bean.ItemPickUpBean;
import com.mcla.kdccollectorbukkit.bean.ItemRemoveBean;
import com.mcla.kdccollectorbukkit.bean.MobSpawnBean;
import com.mcla.kdccollectorbukkit.utils.HttpUtil;
import com.mcla.kdccollectorbukkit.utils.JsonUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

/**
 * @Description:
 * @ClassName: ItemListener
 * @Author: ice_light
 * @Date: 2022/11/13 17:45
 * @Version: 1.0
 */
public class ItemListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void itemSpawn(ItemSpawnEvent event) {
        ItemDropBean mb = new ItemDropBean(true);
        mb.setEssentialInfo(event);
        HttpUtil.postJson(KDCCollectorBukkit.targetUrl, JsonUtil.praseJson(mb));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void itemPickUp(EntityPickupItemEvent event) {
        ItemPickUpBean mb = new ItemPickUpBean(false);
        mb.setEssentialInfo(event);
        mb.setItemName(event.getItem().getName());
        HttpUtil.postJson(KDCCollectorBukkit.targetUrl, JsonUtil.praseJson(mb));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void itemRemove(ItemDespawnEvent event) {
        ItemRemoveBean mb = new ItemRemoveBean(false);
        mb.setEssentialInfo(event);
        HttpUtil.postJson(KDCCollectorBukkit.targetUrl, JsonUtil.praseJson(mb));
    }
}
