package com.apexsoft;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.apex.ams.annotation.AmsBlockingStub;
import com.apex.ams.annotation.AmsFutureStub;
import com.apex.ams.annotation.AmsStub;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.guoyuan.*;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class HelloworldConsumer {
    private static final Logger log = LoggerFactory.getLogger(HelloworldConsumer.class);

    //同步客户端
    @AmsBlockingStub
    private ServiceGrpc.ServiceBlockingStub stub;

    //异步客户端
    @AmsStub
    private ServiceGrpc.ServiceStub asyncStub;

    //异步客户端
    @AmsFutureStub
    private ServiceGrpc.ServiceFutureStub futureStub;

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

    //上传
    public void download() throws Exception {
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
        }finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                }
            }
        }

    }

    //下载
    public JSONObject upload(){
        //GRPC使用的异步请求，所以需要自己加同步锁
        final CountDownLatch count = new CountDownLatch(1);

        final JSONObject[] data = new JSONObject[1];
        //构建异步请求
        StreamObserver<StreamRequest> reqObserver = asyncStub.upload(new StreamObserver<NormalResponse>() {
            @Override
            public void onNext(NormalResponse value) {
                //监听响应报文
                try {
                    data[0] = JSON.parseObject(JsonFormat.printer().print(value));
                } catch (InvalidProtocolBufferException e) {
                    log.error(e.getMessage(),e);
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error(t.getMessage(),t);
                data[0] = new JSONObject(){{
                    put("code",-1);
                    put("note",t.getMessage());
                }};
                //生产者通知异常，释放锁
                count.countDown();
            }

            @Override
            public void onCompleted() {
                //响应报文处理结束，释放锁
                count.countDown();
            }
        });

        //发请求报文
        InputStream fis = null;
        try {
            File file = new File("data-upload.txt");
            fis = new FileInputStream(file);

            //输出文件信息
            StreamRequest.Builder reqBuilder = StreamRequest.newBuilder();
            reqBuilder.setFilename(file.getName());
            reqObserver.onNext(reqBuilder.build());

            //输出文件长度
            reqBuilder.setFileLength(fis.available());
            reqObserver.onNext(reqBuilder.build());

            //输出文件流
            int length;
            byte[] bytes = new byte[1024];
            int i=0;
            while ((length = fis.read(bytes)) != -1) {
                reqBuilder.setDataBlock(ByteString.copyFrom(bytes, 0, length));
                reqObserver.onNext(reqBuilder.build());

                //模拟异常
                /*if(i++==3){
                    Thread.sleep(2000);
                    throw new Exception("消费者模拟异常");
                }*/

            }
            reqObserver.onCompleted();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            reqObserver.onError(new StatusException(Status.INTERNAL));
            //如果消费者在请求报文处理出现异常，也需要释放同步锁
            //因为消费者往生产者抛出异常后，生产者不会调用onCompleted，就无法释放同步锁
            count.countDown();
            data[0] = new JSONObject(){{
                put("code",-1);
                put("note",e.getMessage());
            }};
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        try {
            count.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e.getMessage(),e);
        }
        return data[0];

    }
}

