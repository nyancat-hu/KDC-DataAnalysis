package com.mcla.kdccollectorbukkit.config;

import com.mcla.kdccollectorbukkit.KDCCollectorBukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Description: com.mcla.kdccollectorbukkit.Config  // 类说明，在创建类时要填写
 * @ClassName: com.mcla.kdccollectorbukkit.Config    // 类名，会自动填充
 * @Author: ice          // 创建者
 * @Date: 2022/10/12 21:12   // 时间
 * @Version: 1.0     // 版本
 */
public class Config {
    public static void loadConfig(String ymlName) {
        String path = KDCCollectorBukkit.snowkPlugin.getDataFolder().getAbsolutePath()+File.separator+ymlName;
        File file = new File(path);
        if (!file.exists()) {
            KDCCollectorBukkit.snowkPlugin.saveDefaultConfig();
        }
        try {
            FileInputStream fileinputstream = new FileInputStream(file);
            YamlConfiguration config = new YamlConfiguration();
            config.load(new InputStreamReader(fileinputstream, StandardCharsets.UTF_8));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static double getDouble(String label) {
        return KDCCollectorBukkit.snowkPlugin.getConfig().getDouble(label);
    }
    public static String getString(String label) {
        return KDCCollectorBukkit.snowkPlugin.getConfig().getString(label).replaceAll("&", "¡ì");
    }
    public static boolean getBoolean(String label) {
        return KDCCollectorBukkit.snowkPlugin.getConfig().getBoolean(label);
    }
    public static List<String> getStringList(String label) {
        return KDCCollectorBukkit.snowkPlugin.getConfig().getStringList(label);
    }
    public static List<Integer> getIntegerList(String label) {
        return KDCCollectorBukkit.snowkPlugin.getConfig().getIntegerList(label);
    }
}
