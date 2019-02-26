package com.apexsoft;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.apex.ams.annotation.AmsBlockingStub;
import com.google.protobuf.util.JsonFormat;
import com.guoyuan.NormalRequest;
import com.guoyuan.NormalResponse;
import com.guoyuan.ServiceGrpc;
import com.guoyuan.StreamResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.UUID;

@Service
public class HelloworldConsumer {
    private static final Logger log = LoggerFactory.getLogger(HelloworldConsumer.class);

    @AmsBlockingStub
    private ServiceGrpc.ServiceBlockingStub stub;

    public JSONObject normal() {
        try {
            NormalRequest req = NormalRequest.newBuilder().setParam("test").build();
            NormalResponse resp = stub.normal(req);
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

    public void download() throws IOException {
        NormalRequest req = NormalRequest.newBuilder().setParam("test").build();
        Iterator<StreamResponse> ite = stub.download(req);
        File file;
        OutputStream fos = null;
        try {
            while (ite.hasNext()) {
                StreamResponse resp = ite.next();
                switch (resp.getDataCase()) {
                    case FILENAME:
                        file = new File(UUID.randomUUID().toString() + resp.getFilename());
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        fos = new FileOutputStream(file);
                        break;
                    case FILELENGTH:
                        log.info("接收到文件长度：{} byte", resp.getFileLength());
                        break;
                    case DATABLOCK:
                        fos.write(resp.getDataBlock().toByteArray());
                        break;


                }
            }
        } finally {
            if (fos != null) {
                fos.close();
            }
        }

    }
}

