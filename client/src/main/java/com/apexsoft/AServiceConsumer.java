package com.apexsoft;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.apex.ams.util.AasCommService;
import com.apexsoft.aas.service.model.*;

import java.io.*;
import java.util.HashMap;

/**
 * Created by Administrator on 2019/1/8.
 */
public class AServiceConsumer {

    public static JSONObject upload() {
        File file = new File("data-upload.txt");
        try (InputStream fis = new FileInputStream(file)) {
            AUploadRequest req = new AUploadRequest() {{
                UploadFileInfo fileInfo = new UploadFileInfo();
                fileInfo.setFileLength(Long.valueOf(fis.available()));
                fileInfo.setFileName(file.getName());
                setFileInfo(fileInfo);
                setInputStream(fis);
            }};
            AUploadResponse result = AasCommService.upload("com.guoyuan", "com.guoyuan", "upload", req);
            return (JSONObject) JSON.toJSON(result);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static JSONObject download() {
        ADownloadRequest req = new ADownloadRequest();
        req.setParams(new HashMap<String, Object>() {{
            put("file", 1111);
        }});
        ADownloadResponse response = AasCommService.download("com.guoyuan", "com.guoyuan", "download", req);
        File file = new File(response.getFileInfo().getFileName());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (InputStream is = response.getInputStream();
             FileOutputStream fos = new FileOutputStream(file)) {
            byte[] bytes = new byte[1024];
            int len;
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (JSONObject) JSON.toJSON(response);

    }

    public static JSONObject normal() {
        ARequest req = new ARequest();
        req.setParams(new HashMap<String, Object>() {{
            put("file", 1111);
        }});
        AResponse result = AasCommService.sendRequest("com.guoyuan", "com.guoyuan", "normal", req);
        return (JSONObject) JSON.toJSON(result);
    }

}
