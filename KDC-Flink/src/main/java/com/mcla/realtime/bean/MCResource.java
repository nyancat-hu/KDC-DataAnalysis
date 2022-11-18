package com.mcla.realtime.bean;

import java.io.Serializable;

public class MCResource implements Serializable {
    // 位置信息 x 和 y
    public double x,y;
    // 编号 type
    public String type;
    // 权重 alpha
    public int alpha;
    // 默认无参构造函数  （必须有这个，不然进行keyby分组的时候会报错）
    public MCResource() {}
    // 带参构造函数
    public MCResource(double x, double y, String type, int alpha)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        this.alpha = alpha;
    }

    // 定义所有获得方法
    public String getId()
    {
        return type;
    }
    public double getX() { return x; }
    public double getY(){
        return y;
    }
    public int getAlpha(){
        return alpha;
    }

    // 定义所有设置方法
    public void setId(String type) {
        this.type = type;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setAlpha(int alpha) { this.alpha = alpha; }

    // 重写 tostring()
    @Override
    public String toString()
    {
        return "MCResource{" + "type='" + type + '\'' + ", x=" + x + ", y=" + y + ", alpha=" + alpha +'}';
    }
}
