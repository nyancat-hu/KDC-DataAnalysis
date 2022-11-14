package com.mcla.kdccollectorbukkit.bean;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;

/**
 * @Description: 生物生成Bean
 * @ClassName: MobSpawnBean
 * @Author: ice_light
 * @Date: 2022/11/13 14:36
 * @Version: 1.0
 */
public class MobSpawnBean extends MobBean{
    private CreatureSpawnEvent.SpawnReason spawnReason;
    private int health;

    public CreatureSpawnEvent.SpawnReason getSpawnReason() {
        return spawnReason;
    }

    public void setSpawnReason(CreatureSpawnEvent.SpawnReason spawnReason) {
        this.spawnReason = spawnReason;
    }


    @Override
    public String toString() {
        return "MobSpawnBean{" +
                "eventName='" + eventName + '\'' +
                ", entityName='" + entityName + '\'' +
                ", serverName='" + serverName + '\'' +
                ", isSpawn=" + isSpawn +
                ", spawnReason=" + spawnReason +
                ", health=" + health +
                '}';
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public MobSpawnBean(boolean isSpawn) {
        this.isSpawn = isSpawn;
    }

}
