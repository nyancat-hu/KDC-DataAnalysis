package com.mcla.kdccollectorbukkit.listeners;

import com.mcla.kdccollectorbukkit.KDCCollectorBukkit;
import com.mcla.kdccollectorbukkit.bean.ItemDropBean;
import com.mcla.kdccollectorbukkit.bean.ItemPickUpBean;
import com.mcla.kdccollectorbukkit.bean.TileBlockDamageBean;
import com.mcla.kdccollectorbukkit.bean.TileBlockPlaceBean;
import com.mcla.kdccollectorbukkit.utils.HttpUtil;
import com.mcla.kdccollectorbukkit.utils.JsonUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

/**
 * @Description:
 * @ClassName: TileListener
 * @Author: ice_light
 * @Date: 2022/11/13 17:45
 * @Version: 1.0
 */
public class TileListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void itemSpawn(BlockPlaceEvent event) {
        if (!event.getBlock().getState().getClass().getName().endsWith("CraftBlockState")){
            // 如果这是一个tileEntity，则继续执行下去
            TileBlockPlaceBean mb = new TileBlockPlaceBean(true);
            mb.setEssentialInfo(event);
            HttpUtil.postJson(KDCCollectorBukkit.targetUrl, JsonUtil.praseJson(mb));
//            System.out.println(mb);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void itemPickUp(BlockBreakEvent event) {
        if (!event.getBlock().getState().getClass().getName().endsWith("CraftBlockState")){
            // 如果这是一个tileEntity，则继续执行下去
            TileBlockDamageBean mb = new TileBlockDamageBean(false);
            mb.setEssentialInfo(event);
            HttpUtil.postJson(KDCCollectorBukkit.targetUrl, JsonUtil.praseJson(mb));
//            System.out.println(mb);
        }
    }
}
