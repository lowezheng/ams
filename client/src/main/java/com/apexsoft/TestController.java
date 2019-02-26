package com.apexsoft;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private HelloworldConsumer consumer;

    @RequestMapping("normal")
    public JSONObject normal() {
        return consumer.normal();
    }

    @RequestMapping("download")
    public JSONObject download() {
        try {
            consumer.download();
            return new JSONObject() {{
                put("note", "下载成功");
            }};
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject() {{
                put("note", "下载失败," + e.getMessage());
            }};
        }
    }

    @RequestMapping("upload")
    public JSONObject upload() {
        return consumer.upload();
    }

    @RequestMapping("simple/normal")
    public JSONObject simpleNormal() {
        return AServiceConsumer.normal();
    }

    @RequestMapping("simple/download")
    public JSONObject simpleDownload() {
        try {
            return AServiceConsumer.download();

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject() {{
                put("note", "下载失败," + e.getMessage());
            }};
        }
    }

    @RequestMapping("simple/upload")
    public JSONObject simpleUpload() {
        return AServiceConsumer.upload();
    }
}
