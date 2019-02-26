package com.apexsoft;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TestController {

    @Autowired
    private HelloworldConsumer consumer;

    @RequestMapping("normal")
    public JSONObject normal(){
        return consumer.normal();
    }

    @RequestMapping("download")
    public JSONObject test(){
        try {
            consumer.download();
            return new JSONObject(){{
                put("note","下载成功");
            }};
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONObject(){{
                put("note","下载失败,"+e.getMessage());
            }};
        }
    }
}
