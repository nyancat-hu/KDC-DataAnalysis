package com.mcla.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pravega.client.stream.EventStreamWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Service
public class LogServiceImpl {
    @Autowired
    EventStreamWriter<String> writer;

    @Autowired
    ObjectMapper om;




    public void printLog(Map<String,String> map){
        //1.打印输出到控制台

        Object obj = JSONArray.toJSON(map);
        //输出数据到pravega
        writer.writeEvent("routing-key",obj.toString());
        // 打印到控制台
//        System.out.println(obj.toString());
        //2.落盘   借助记录日志的第三方框架 log4j [logback]
//        log.info(jsonLog);
        //3.将生成的日主发送到kafka对应的主题中
//        kafkaTemplate.send("ods_base_log",jsonLog);
    }
    public void printLog(Object ob){
        // 向kafka发送数据
        try {
            //输出数据到pravega
            writer.writeEvent("routing-key",om.writeValueAsString(ob));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
