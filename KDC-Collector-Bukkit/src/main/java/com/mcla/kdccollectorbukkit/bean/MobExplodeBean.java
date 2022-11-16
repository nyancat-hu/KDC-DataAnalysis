package com.mcla.kdccollectorbukkit.bean;

/**
 * @Description: 生物爆炸事件
 * @ClassName: MobExplodeBean
 * @Author: ice_light
 * @Date: 2022/11/13 14:36
 * @Version: 1.0
 */
public class MobExplodeBean extends MobBean{
    private float yield;

    public float getYield() {
        return yield;
    }

    public MobExplodeBean(boolean b) {
        this.isSpawn = b;
    }

    public void setYield(float yield) {
        this.yield = yield;
    }

}
