package com.mcla.kdccollectorbukkit.listeners;

import com.bergerkiller.bukkit.common.events.EntityAddEvent;
import com.mcla.kdccollectorbukkit.KDCCollectorBukkit;
import com.mcla.kdccollectorbukkit.bean.*;
import com.mcla.kdccollectorbukkit.tasks.EntityCleanTask;
import com.mcla.kdccollectorbukkit.utils.HikariCPUtils;
import com.mcla.kdccollectorbukkit.utils.HttpUtil;
import com.mcla.kdccollectorbukkit.utils.JsonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

/**
 * @Description: 限制实体或清理地面掉落物的监听器
 * @ClassName: LimitListener
 * @Author: ice_light
 * @Date: 2022/11/16 22:33
 * @Version: 1.0
 */
public class LimitListener implements Listener{
    // 这个监听器负责给聚类中心所在的区块实施限制策略
    // 1.对于掉落物，向区块内的玩家发送信息，提示若不捡起地面掉落物
    //    则会在30s内对区块掉落物进行大清洗
    // 2.对于实体，直接限制区块内的实体生成
    // 3.对于带信息方块，限制该方块的放置，并提示玩家
    private DensityBean itemDb;
    private DensityBean entityDb;
    private DensityBean tileEntityDb;


    @EventHandler
    public void tickUpdatePosition(PluginEnableEvent event) {
        // 该任务用于定时从数据库更新坐标数据，每一秒钟更新一次
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(KDCCollectorBukkit.snowkPlugin, () -> {
            List<DensityBean> densityBean = HikariCPUtils.getDensityBean();
            if(densityBean!=null){
                entityDb = densityBean.get(0);
                itemDb = densityBean.get(1);
                tileEntityDb = densityBean.get(2);
            }
        }, 0L, 20L);
    }

    @EventHandler
    public void creatureSpawn(CreatureSpawnEvent event) {
        if(!(event.getEntity() instanceof Player) && !(event.getEntity() instanceof Item)){
            if(entityDb!=null){
                // 如果该区块被判定为密集区块，则禁止该区块的生物生成，并向附近玩家广播消息
                // 告诉玩家XX秒后将会进行区块生物清理
                if(!EntityCleanTask.isCleaning){
                    // 在Task种广播消息给附近的玩家
                    new EntityCleanTask(entityDb).runTaskLater(KDCCollectorBukkit.snowkPlugin,150L);
                }
            }
        }
    }
    @EventHandler
    public void itemSpawn(ItemSpawnEvent event) {
        // 如果该区块被判定为密集区块，则启动清理程序，向附近玩家广播消息

    }

    @EventHandler
    public void itemSpawn(BlockPlaceEvent event) {
        if (!event.getBlock().getState().getClass().getName().endsWith("CraftBlockState")){
            // 如果该区块被判定为密集tile区块，则禁止玩家继续放置tileEntity
        }
    }

}
