package com.apexsoft;

import com.alibaba.fastjson.JSON;
import com.apexsoft.aas.service.annotations.ABusiness;
import com.apexsoft.aas.service.annotations.AService;
import com.apexsoft.aas.service.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ABusiness(namespace = "com.guoyuan", pkg = "com.guoyuan")
public class HelloworldAService {
    private static final Logger log = LoggerFactory.getLogger(HelloworldAService.class);

    //普通请求
    @AService(name = "normal")
    public AResponse service(ARequest request) {
        log.info(JSON.toJSONString(request));

        AResponse response = new AResponse();
        response.setCode(1);
        response.setNote("测试报文");
        Map<String, Object> data = new HashMap<>();
        data.put("param1", 11);
        data.put("user",new User(){{
            setAge(1);
            setName("dddd");
        }});
        response.setData(data);
        List<User> users =new ArrayList<>();
        users.add(new User(){{
            setAge(1);
            setName("11111");
        }});
        users.add(new User(){{
            setAge(2);
            setName("22222");
        }});


        response.setRecordsEx(users);
        return response;
    }
    //上传请求
    @AService(name = "upload")
    public AUploadResponse upload(AUploadRequest request) {
        UploadFileInfo fileInfo = request.getFileInfo();
        log.info(fileInfo.toString());
        File file = new File(fileInfo.getFileName());
        AUploadResponse response = new AUploadResponse();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] bytes = new byte[1024];
            int len;
            while ((len = request.getInputStream().read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            response.setCode(1);
            response.setFilecode(file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            response.setCode(-1);
            response.setNote(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            response.setCode(-1);
            response.setNote(e.getMessage());
        }
        return response;
    }

    //下载请求
    @AService(name = "download")
    public ADownloadResponse download(ADownloadRequest request) {
        log.info(request.toString());
        ADownloadResponse response = new ADownloadResponse();
        File file = new File("data-download.txt");
        try {
            FileInputStream fis = new FileInputStream(file);
            DownloadFileInfo fileInfo = new DownloadFileInfo();
            fileInfo.setFileLength(Long.valueOf(fis.available()));
            fileInfo.setFileName(file.getName());
            response.setFileInfo(fileInfo);
            response.setInputStream(fis);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return response;
    }
}
