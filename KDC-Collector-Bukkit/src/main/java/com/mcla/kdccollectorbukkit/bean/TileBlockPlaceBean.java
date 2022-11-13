package com.mcla.kdccollectorbukkit.bean;

/**
 * @Description: tile方块放置时bean
 * @ClassName: TileBlockPlaceBean
 * @Author: ice_light
 * @Date: 2022/11/13 18:49
 * @Version: 1.0
 */
public class TileBlockPlaceBean extends TileBean{
    public TileBlockPlaceBean(boolean b) {
        this.isPlace = b;
    }

    @Override
    public String toString() {
        return "TileBlockPlaceBean{" +
                "eventName='" + eventName + '\'' +
                ", blockName='" + blockName + '\'' +
                ", location=" + location +
                ", serverName='" + serverName + '\'' +
                ", isPlace=" + isPlace +
                ", entityNums=" + entityNums +
                ", tileEntityNums=" + tileEntityNums +
                ", chunkX=" + chunkX +
                ", chunkZ=" + chunkZ +
                '}';
    }
}
