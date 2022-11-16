package com.mcla.kdccollectorbukkit.bean;

import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * @Description: 生物死亡bean
 * @ClassName: MobDeathBean
 * @Author: ice_light
 * @Date: 2022/11/13 14:36
 * @Version: 1.0
 */
public class MobDeathBean extends MobBean{
    private int droppedExp;
    private int health;

    public MobDeathBean(boolean isSpawn) {
        this.isSpawn = isSpawn;
    }

    public void setDroppedExp(int droppedExp) {
        this.droppedExp = droppedExp;
    }

    public int getDroppedExp() {
        return droppedExp;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
