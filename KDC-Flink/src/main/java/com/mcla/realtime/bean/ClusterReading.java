package com.mcla.realtime.bean;

public class ClusterReading {
    //位置信息 x 和 y
    public double x,y;
    // 编号 id
    public int id;
    // 默认无参构造函数  （必须有这个，不然进行keyby分组的时候会报错）
    public ClusterReading()  {    }
    // 带参构造函数
    public ClusterReading(double x, double y,int id)
    {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    // 定义所有获得方法
    public int getId()
    {
        return id;
    }

    public double getX() {
        return x;
    }
    public double getY(){
        return y;
    }

    // 定义所有设置方法
    public void setId(int id) {
        this.id = id;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    // 重写 tostring()
    @Override
    public String toString()
    {
        return "ClusterReading{" + "id='" + id + '\'' + ", x=" + x + ", y=" + y + '}';
    }
}

