package com.mcla.realtime.bean;

/**
 * @Description: 存放用于聚类前的点
 * @ClassName: DbscanBean
 * @Author: ice_light
 * @Date: 2022/11/18 18:36
 * @Version: 1.0
 */
public class DbscanBean {
    public DbscanBean(double x, double y, double z, int alpha) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.alpha = alpha;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    // 位置信息 x 和 y
    public double x,y,z;
    // 带参构造函数
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    // 权重 alpha
    public int alpha = 1;

}