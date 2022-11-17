package com.mcla.kdccollectorbukkit.bean;

/**
 * @Description: 存放聚类信息
 * @ClassName: DensityBean
 * @Author: ice_light
 * @Date: 2022/11/17 12:02
 * @Version: 1.0
 */
public class DensityBean {
    private String type;
    private String centerPosition;
    private String chunkPosition;
    private boolean limit = true;


    public boolean isLimit() {
        return limit;
    }

    public void setLimit(boolean limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "DensityBean{" +
                "type='" + type + '\'' +
                ", centerPosition='" + centerPosition + '\'' +
                ", chunkPosition='" + chunkPosition + '\'' +
                ", limit=" + limit +
                '}';
    }

    public DensityBean(String type, String centerPosition, String chunkPosition) {
        this.type = type;
        this.centerPosition = centerPosition;
        this.chunkPosition = chunkPosition;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCenterPosition() {
        return centerPosition;
    }

    public void setCenterPosition(String centerPosition) {
        this.centerPosition = centerPosition;
    }

    public String getChunkPosition() {
        return chunkPosition;
    }

    public void setChunkPosition(String chunkPosition) {
        this.chunkPosition = chunkPosition;
    }
}
