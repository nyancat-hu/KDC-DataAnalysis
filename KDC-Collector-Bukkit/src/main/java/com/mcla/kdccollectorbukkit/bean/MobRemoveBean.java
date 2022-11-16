package com.mcla.kdccollectorbukkit.bean;

/**
 * @Description: 实体被服务器删除或清理时的Bean
 * @ClassName: MobRemoveBean
 * @Author: ice_light
 * @Date: 2022/11/13 16:56
 * @Version: 1.0
 */
public class MobRemoveBean extends MobBean{
    public MobRemoveBean(boolean isSpawn) {
        this.isSpawn = isSpawn;
    }

}
