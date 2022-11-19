package com.mcla.kdccollectorbukkit.tasks;

import com.mcla.kdccollectorbukkit.bean.DensityBean;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.MessageFormat;
import java.util.*;

/**
 * @Description:
 * @ClassName: ItemCleanTask
 * @Author: ice_light
 * @Date: 2022/11/17 13:46
 * @Version: 1.0
 */
public class ItemCleanTask extends BukkitRunnable {
    public static volatile boolean isCleaning = false;

    private final List<String> chunkList = new LinkedList<>();


    public ItemCleanTask(DensityBean itemDb) {
        isCleaning = true;
        String[] splitPosition = itemDb.getCenterPosition().split(";");
        String[] splitChunk = itemDb.getChunkPosition().split(";");
        this.chunkList.addAll(Arrays.asList(splitChunk));
        //在此处加一下广播，广播给区块中的每个玩家
        for (String s : splitChunk) {
            String[] split = s.split(",");
            Arrays.stream(Bukkit.getWorld("world").getChunkAt(Integer.parseInt(split[0]),Integer.parseInt(split[1])).getEntities())
                    .filter(entity -> (entity instanceof Player)&&split[2].equals("Y"))
                    .forEach(entity -> {
                        entity.sendMessage("§b检测到您附近掉落物密集，请及时拾取，将在 15 秒后进行进行§a§l 物品清理 §b...");
                        for (String position : splitPosition) {
                            entity.sendMessage("        - §c§l物品密集中心坐标点§b x-z: §a§l[§e§l " + position + "§a§l"  + " ]");
                        }
                    });
        }
    }

    @Override
    public void run() {
        // 下面这一段要改成只对指定区块进行清理
        Bukkit.getWorlds().stream()
                .filter(world -> world.getName().equals("world"))
                .flatMap(world -> Arrays.stream(world.getLoadedChunks()))
                .filter(chunk -> {
                    for (String s : chunkList) {
                        String[] split = s.split(",");
                        if((chunk.getX() == Integer.parseInt(split[0]) && chunk.getZ() == Integer.parseInt(split[1]))&&split[2].equals("Y")) return true;
                    }
                    return false;
                })
                .map(chunk -> Arrays.asList(chunk.getEntities()))
                .forEach(entities -> {
                    Map<EntityType, List<Entity>> types = new HashMap<>();
                    entities.forEach(entity -> {
                        EntityType type = entity.getType();
                        List<Entity> list = types.get(type);
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        if(entity instanceof Item){
                            list.add(entity);
                        }
                        types.remove(type);
                        types.put(type, list);
                    });
                    types.forEach((key, value1) -> {
                        int value = 2;
                        if (value1.size() < value) {
                            return;
                        }
                        int toClean = value1.size() - value;
                        for (int i = 0; i < toClean; i++) {
                            value1.get(i).remove();
                        }
                    });
                    Arrays.stream(Bukkit.getWorld("world").getLoadedChunks())
                            .filter(chunk -> {
                                for (String s : chunkList) {
                                    String[] split = s.split(",");
                                    if((chunk.getX() == Integer.parseInt(split[0]) && chunk.getZ() == Integer.parseInt(split[1]))&&split[2].equals("Y")) return true;
                                }
                                return false;
                            })
                            .map(chunk -> chunk.getEntities())
                            .forEach(entitiesA -> {
                                Arrays.stream(entitiesA).forEach(entity-> {if((entity instanceof Player)) entity.sendMessage("§b已清理区块高密度实体，清理数量为：" + entities.size());});
                            });
                });
        isCleaning = false;
    }
}
