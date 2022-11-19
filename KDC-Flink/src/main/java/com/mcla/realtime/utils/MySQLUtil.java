package com.mcla.realtime.utils;

import java.sql.*;

/**
 * Author: Felix
 * Date: 2021/2/1
 * Desc: 从MySQL数据中查询数据的工具类
 * 完成ORM，对象关系映射
 * O：Object对象       Java中对象
 * R：Relation关系     关系型数据库
 * M:Mapping映射      将Java中的对象和关系型数据库的表中的记录建立起映射关系
 * 数据库                 Java
 * 表t_student           类Student
 * 字段id，name           属性id，name
 * 记录 100，zs           对象100，zs
 * ResultSet(一条条记录)             List(一个个Java对象)
 */
public class MySQLUtil {
    public static void truncateTable(String tableName) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //注册驱动
            Class.forName("com.mysql.jdbc.Driver");
            //创建连接
            conn = DriverManager.getConnection(
                "jdbc:mysql://topview102:3306/mc_streaming?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false",
                "root",
                "430525");
            String sql = "truncate table "+tableName;
            //创建数据库操作对象
            ps = conn.prepareStatement(sql);
            //执行SQL语句
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("清理数据库表单失败");
        } finally {
            //释放资源
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    public static void main(String[] args) {
//        List<TableProcess> list = queryList("select * from table_process", TableProcess.class, true);
//        for (TableProcess tableProcess : list) {
//            System.out.println(tableProcess);
//        }
//    }
}
