package com.mcla.kdccollectorbukkit.tasks;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description:
 * @ClassName: ItemCleanTask
 * @Author: ice_light
 * @Date: 2022/11/17 13:46
 * @Version: 1.0
 */
public class ItemCleanTask extends BukkitRunnable {
//    private Map<World, Set<Item>> data;
//
//    ItemCleanTask(Map<World, Set<Item>> data) {
//        this.data = data;
//    }

    @Override
    public void run() {
//        Set<Item> cleanItem = new HashSet<>();
//        data.forEach((key, itemSet) -> itemSet.forEach(item -> {
//            ItemStack itemStack = item.getItemStack();
//            if (Main.getInstance().getConfigManager().isCheckValueEnabled()) {
//                if (!Main.getInstance().getScriptManager().isNeedToClean(item)) {
//                    return;
//                }
//            }
//            if (isInBlackList(itemStack)) {
//                return;
//            }
//            cleanItem.add(item);
//        }));
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                cleanItem.forEach(Entity::remove);
//                if (Main.getInstance().getConfigManager().isEnableCleanBroadcast()) {
//                    Main.getInstance().getBroadcaster().broadcast(MessageFormat.format(Main.getInstance().getConfigManager().getCleanDoneMessage(), String.valueOf(cleanItem.size())));
//                }
//            }
//        }.runTask(Main.getInstance());
//        Main.getInstance().getDropSkipManager().clear();
    }

//    private boolean isInBlackList(ItemStack itemStack) {
//        List<String> blackList = Main.getInstance().getConfigManager().getBlackList();
//        for (String black : blackList) {
//            if (black.startsWith("@")) {
//                String blackType = black.substring(1);
//                if (itemStack.getType().toString().contains(blackType)) {
//                    return true;
//                }
//            } else {
//                if (itemStack.getType().toString().equalsIgnoreCase(black)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
}
