package com.mcla.kdccollectorbukkit.bean;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityEvent;

/**
 * @Description: 物品Bean
 * @ClassName: ItemBean
 * @Author: ice_light
 * @Date: 2022/11/13 17:43
 * @Version: 1.0
 */
public abstract class ItemBean {
    String eventName;
    String itemName;
    int x;
    int y;
    int z;
    String world;
    String tag;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    String serverName;
    boolean isSpawn;
    int itemAmount;

    public int getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(int itemAmount) {
        this.itemAmount = itemAmount;
    }

    int chunkX;
    int chunkZ;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setEssentialInfo(EntityEvent event){
        setServerName(Bukkit.getServerName());
        setX((int) event.getEntity().getLocation().getX());
        setY((int) event.getEntity().getLocation().getY());
        setZ((int) event.getEntity().getLocation().getZ());
        setWorld(event.getEntity().getLocation().getWorld().getName());
        setItemName(event.getEntity().getName());
        setEventName(event.getEventName());
        setTag(String.valueOf(event.getEntity().hashCode()));
//        setEntityNums(event.getEntity().getLocation().getChunk().getEntities().length);
//        setTileEntityNums(event.getEntity().getLocation().getChunk().getTileEntities().length);
        setChunkX(event.getEntity().getLocation().getChunk().getX());
        setChunkZ(event.getEntity().getLocation().getChunk().getZ());
    }
}
