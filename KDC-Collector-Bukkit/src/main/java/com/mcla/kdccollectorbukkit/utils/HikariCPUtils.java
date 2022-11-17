package com.mcla.kdccollectorbukkit.utils;

import com.mcla.kdccollectorbukkit.bean.DensityBean;
import com.mcla.kdccollectorbukkit.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: HikariCP数据库连接池工具类
 * @ClassName: HikariCPUtils
 * @Author: ice_light
 * @Date: 2022/11/16 19:10
 * @Version: 1.0
 */
public class HikariCPUtils {
    private static HikariDataSource sqlConnectionPool;

    public static void configsqlConnectionPool(){
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");

        // 从配置文件中读取HikariCP配置
        hikariConfig.setConnectionTimeout(Config.getLong("connectionTimeout"));
        hikariConfig.setMinimumIdle(Config.getInt("minimumIdle"));
        hikariConfig.setMaximumPoolSize(Config.getInt("maximumPoolSize"));

        // 读取配置文件中的Mysql配置

        String URI = "jdbc:mysql://"
                +Config.getString("address") + ":"
                +Config.getString("port") + "/"
                +Config.getString("database")
                +"?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai";
        hikariConfig.setJdbcUrl(URI);
        hikariConfig.setUsername(Config.getString("user"));
        hikariConfig.setPassword(Config.getString("password"));

        // 开启事务的自动提交
        hikariConfig.setAutoCommit(true);

        sqlConnectionPool = new HikariDataSource(hikariConfig);
    }

    public static Connection getConnection(){
        try{
            return sqlConnectionPool.getConnection();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<DensityBean> getDensityBean(){
        Connection connection = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try{
            connection = getConnection();
            String sql = "select * from DensityTable";
            assert connection != null;
            psmt = connection.prepareStatement(sql);
            rs = psmt.executeQuery();
            List<DensityBean> db = new ArrayList<>();
            while(rs.next()){
                db.add(new DensityBean(rs.getString("Name"),rs.getString("CenterPosition"),rs.getString("ChunkLocation")));
            }
            return db;
        }catch(SQLException throwables){
            throwables.printStackTrace();
        }
        finally{
            if(connection!=null) {
                try {
                    connection.close();
                    if(psmt!=null)psmt.close();
                    if(rs !=null)rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return null;
    }
}
