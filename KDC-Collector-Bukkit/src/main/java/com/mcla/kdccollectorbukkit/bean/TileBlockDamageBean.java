package com.mcla.kdccollectorbukkit.bean;

/**
 * @Description: tilebean破坏时的bean
 * @ClassName: TileBlockDamageBean
 * @Author: ice_light
 * @Date: 2022/11/13 18:50
 * @Version: 1.0
 */
public class TileBlockDamageBean extends TileBean{
    public TileBlockDamageBean(boolean b) {
        this.isPlace = b;
    }

    @Override
    public String toString() {
        return "TileBlockDamageBean{" +
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
