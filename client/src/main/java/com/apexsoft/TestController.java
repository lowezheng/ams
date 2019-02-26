package com.apexsoft;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private HelloworldConsumer consumer;

    @RequestMapping("test")
    public JSONObject test(){
        return consumer.hello();
    }
}
