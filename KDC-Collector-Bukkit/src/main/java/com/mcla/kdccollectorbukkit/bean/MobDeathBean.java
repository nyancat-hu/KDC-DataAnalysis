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
    private List<ItemStack> drops;
    private EntityEquipment equipment;
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

    @Override
    public String toString() {
        return "MobDeathBean{" +
                "eventName='" + eventName + '\'' +
                ", entityName='" + entityName + '\'' +
                ", location=" + location +
                ", serverName='" + serverName + '\'' +
                ", isSpawn=" + isSpawn +
                ", droppedExp=" + droppedExp +
                ", drops=" + drops +
                ", equipment=" + equipment +
                ", health=" + health +
                '}';
    }

    public void setDrops(List<ItemStack> drops) {
        this.drops = drops;
    }


    public void setEquipment(EntityEquipment equipment) {
        this.equipment = equipment;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
