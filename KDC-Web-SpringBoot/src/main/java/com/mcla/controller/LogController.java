package com.mcla.controller;

import com.mcla.bean.StateBean;
import com.mcla.service.impl.LogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class LogController {
    @Autowired
    private LogServiceImpl logService;

    @RequestMapping("/log")
    public String logger(@RequestBody Map<String,String> map){
        logService.printLog(map);
        return "success";
    }
    @RequestMapping("/state")
    public String logState(@RequestBody StateBean sb){
        logService.printLog(sb);
//        System.out.println(sb);
        return "success";
    }
}
