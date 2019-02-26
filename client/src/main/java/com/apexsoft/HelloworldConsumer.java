package com.apexsoft;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.apex.ams.annotation.AmsBlockingStub;
import com.google.protobuf.util.JsonFormat;
import com.guoyuan.helloworld.HelloRequest;
import com.guoyuan.helloworld.HelloResponse;
import com.guoyuan.helloworld.TestServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HelloworldConsumer {
    private static final Logger log = LoggerFactory.getLogger(HelloworldConsumer.class);

    @AmsBlockingStub
    private TestServiceGrpc.TestServiceBlockingStub stub;

    public JSONObject hello() {
        try {
            HelloRequest req = HelloRequest.newBuilder().setFunc("test")
                    .build();
            HelloResponse resp = stub.handle(req);
            log.info(resp.toString());
            return JSON.parseObject(JsonFormat.printer().print(resp));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            JSONObject result = new JSONObject();
            result.put("code", -1);
            result.put("note", e.getMessage());
            return result;
        }

    }
}

