package com.mcla.kdccollectorbukkit.bean;

/**
 * @Description:
 * @ClassName: ItemRemoveBean
 * @Author: ice_light
 * @Date: 2022/11/13 17:44
 * @Version: 1.0
 */
public class ItemRemoveBean extends ItemBean{
    @Override
    public String toString() {
        return "ItemRemoveBean{" +
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

    public ItemRemoveBean(boolean isSpawn) {
        this.isSpawn = isSpawn;
    }
}
