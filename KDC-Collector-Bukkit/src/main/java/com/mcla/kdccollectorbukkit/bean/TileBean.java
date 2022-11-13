package com.mcla.kdccollectorbukkit.bean;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;

/**
 * @Description: tile的Bean
 * @ClassName: TileBean
 * @Author: ice_light
 * @Date: 2022/11/13 17:43
 * @Version: 1.0
 */
public abstract class TileBean {
    String eventName;
    String blockName;
    Location location;
    String serverName;
    boolean isPlace;
    int entityNums;
    int tileEntityNums;
    int chunkX;
    int chunkZ;

    public void setEssentialInfo(BlockEvent event){
        setServerName(Bukkit.getServerName());
        setLocation(event.getBlock().getLocation());
        setBlockName(event.getBlock().toString());
        setEventName(event.getEventName());
        setEntityNums(event.getBlock().getLocation().getChunk().getEntities().length);
        setTileEntityNums(event.getBlock().getLocation().getChunk().getTileEntities().length);
        setChunkX(event.getBlock().getLocation().getChunk().getX());
        setChunkZ(event.getBlock().getLocation().getChunk().getZ());
    }
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
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

    public boolean isPlace() {
        return isPlace;
    }

    public void setPlace(boolean place) {
        isPlace = place;
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
}
