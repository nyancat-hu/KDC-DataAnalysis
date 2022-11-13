package com.mcla.kdccollectorbukkit.bean;

/**
 * @Description:
 * @ClassName: ItemDropBean
 * @Author: ice_light
 * @Date: 2022/11/13 17:44
 * @Version: 1.0
 */
public class ItemDropBean extends ItemBean{

    @Override
    public String toString() {
        return "ItemDropBean{" +
                "eventName='" + eventName + '\'' +
                ", itemName='" + itemName + '\'' +
                ", location=" + location +
                ", serverName='" + serverName + '\'' +
                ", isSpawn=" + isSpawn +
                ", entityNums=" + entityNums +
                ", tileEntityNums=" + tileEntityNums +
                ", chunkX=" + chunkX +
                ", chunkZ=" + chunkZ +
                '}';
    }

    public ItemDropBean(boolean isSpawn) {
        this.isSpawn = isSpawn;
    }
}
