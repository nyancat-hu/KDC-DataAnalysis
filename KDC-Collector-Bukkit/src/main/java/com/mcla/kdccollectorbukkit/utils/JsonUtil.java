package com.mcla.kdccollectorbukkit.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * @Description: 打印json
 * @ClassName: JsonUtil
 * @Author: ice_light
 * @Date: 2022/11/13 21:31
 * @Version: 1.0
 */
public class JsonUtil {
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static String praseJson(Object o){
        return gson.toJson(o);
    }
}
