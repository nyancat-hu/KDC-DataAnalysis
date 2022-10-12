package com.mcla.kdccollectorbukkit.bean;

import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;

/**
 * @Description: bean of mob spawn event
 * @ClassName: MobBean
 * @Author: ice_light
 * @Date: 2022/10/12 21:45
 * @Version: 1.0
 */
public class MobBean {
    private String eventName;
    private String entityName;
    private Location location;

    private CreatureSpawnEvent.SpawnReason spawnReason;
    private EntityEquipment equipment;
    private int health;

    @Override
    public String toString() {
        return "MobBean{" +
                "eventName='" + eventName + '\'' +
                ", entityName='" + entityName + '\'' +
                ", location=" + location +
                ", spawnReason=" + spawnReason +
                ", equipment=" + equipment +
                ", health=" + health +
                '}';
    }

    public MobBean() {
        this.eventName = "生物生成";
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public CreatureSpawnEvent.SpawnReason getSpawnReason() {
        return spawnReason;
    }

    public void setSpawnReason(CreatureSpawnEvent.SpawnReason spawnReason) {
        this.spawnReason = spawnReason;
    }

    public EntityEquipment getEquipment() {
        return equipment;
    }

    public void setEquipment(EntityEquipment equipment) {
        this.equipment = equipment;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }



}
