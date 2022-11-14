package com.mcla.kdccollectorbukkit.bean;

/**
 * @Description:
 * @ClassName: ItemPickUpBean
 * @Author: ice_light
 * @Date: 2022/11/13 17:43
 * @Version: 1.0
 */
public class ItemPickUpBean extends ItemBean{
    @Override
    public String toString() {
        return "ItemPickUpBean{" +
                "eventName='" + eventName + '\'' +
                ", itemName='" + itemName + '\'' +
                ", serverName='" + serverName + '\'' +
                ", isSpawn=" + isSpawn +
                ", entityNums=" + entityNums +
                ", tileEntityNums=" + tileEntityNums +
                ", chunkX=" + chunkX +
                ", chunkZ=" + chunkZ +
                '}';
    }

    public ItemPickUpBean(boolean isSpawn) {
        this.isSpawn = isSpawn;
    }
}
