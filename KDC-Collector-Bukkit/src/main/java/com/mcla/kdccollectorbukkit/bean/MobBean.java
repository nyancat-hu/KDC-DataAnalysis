package com.mcla.kdccollectorbukkit.bean;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.EntityEquipment;

/**
 * @Description: 生物相关Bean
 * @ClassName: MobBean
 * @Author: ice_light
 * @Date: 2022/10/12 21:45
 * @Version: 1.0
 */
public abstract class MobBean {
    String eventName;
    String entityName;
    Location location;
    String serverName;
    boolean isSpawn;
    int entityNums;
    int tileEntityNums;
    int chunkX;
    int chunkZ;

    public void setEssentialInfo(EntityEvent event){
        setServerName(Bukkit.getServerName());
        setLocation(event.getEntity().getLocation());
        setEntityName(event.getEntity().getName());
        setEventName(event.getEventName());
        setEntityNums(event.getEntity().getLocation().getChunk().getEntities().length);
        setTileEntityNums(event.getEntity().getLocation().getChunk().getTileEntities().length);
        setChunkX(event.getEntity().getLocation().getChunk().getX());
        setChunkZ(event.getEntity().getLocation().getChunk().getZ());
    }
    public int getEntityNums() {
        return entityNums;
    }

    public void setEntityNums(int entityNums) {
        this.entityNums = entityNums;
    }

    public int getTileEntityNums() {
        return tileEntityNums;
    }

    public void setTileEntityNums(int tileEntityNums) {
        this.tileEntityNums = tileEntityNums;
    }

    public int getChunkX() {
        return chunkX;
    }

    public void setChunkX(int chunkX) {
        this.chunkX = chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public void setChunkZ(int chunkZ) {
        this.chunkZ = chunkZ;
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

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public boolean isSpawn() {
        return isSpawn;
    }

    public void setSpawn(boolean spawn) {
        isSpawn = spawn;
    }

}
