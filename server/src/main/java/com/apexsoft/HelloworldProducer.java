package com.apexsoft;

import com.apex.ams.server.AmsService;
import com.google.protobuf.ByteString;
import com.guoyuan.*;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.UUID;

@AmsService
public class HelloworldProducer extends ServiceGrpc.ServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(HelloworldProducer.class);

    @Override
    public void normal(NormalRequest request, StreamObserver<NormalResponse> responseObserver) {
        log.info("请求报文：{}", request.toString());
        NormalResponse.Builder builder = NormalResponse.newBuilder();
        builder.setCode(1)
                .setNote("成功");
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }


    @Override
    public StreamObserver<StreamRequest> upload(StreamObserver<NormalResponse> responseObserver) {

        return new StreamObserver<StreamRequest>() {
            private File file;
            private OutputStream fos = null;
            NormalResponse.Builder builder = NormalResponse.newBuilder();
            private int i=0;
            @Override
            public void onNext(StreamRequest req) {
                try {
                    switch (req.getDataCase()) {
                        case FILENAME:
                            file = new File(UUID.randomUUID().toString() + req.getFilename());
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            fos = new FileOutputStream(file);
                            break;
                        case FILELENGTH:
                            log.info("接收到文件长度：{} byte", req.getFileLength());
                            break;
                        case DATABLOCK:
                            fos.write(req.getDataBlock().toByteArray());
                            break;
                    }
                    //模拟异常
                    /*if (i++ == 3) {
                        Thread.sleep(2000);
                        throw new Exception("生产者模拟异常");
                    }*/
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                    responseObserver.onError(new StatusException(Status.INTERNAL));
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error(t.getMessage(),t);

            }

            @Override
            public void onCompleted() {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
                builder.setCode(1).setNote("上传成功");
                responseObserver.onNext(builder.build());
                responseObserver.onCompleted();

            }
        };
    }

    @Override
    public void download(NormalRequest request, StreamObserver<StreamResponse> responseObserver) {
        log.info("请求报文：{}", request.toString());
        InputStream fis = null;
        int i =0;
        try {
            File file = new File("data-download.txt");
            fis = new FileInputStream(file);

            //输出文件信息
            StreamResponse.Builder respBuilder = StreamResponse.newBuilder();
            respBuilder.setFilename(file.getName());
            responseObserver.onNext(respBuilder.build());

            //输出文件长度
            respBuilder.setFileLength(fis.available());
            responseObserver.onNext(respBuilder.build());

            //输出文件流
            int length;
            byte[] bytes = new byte[1024];
            while ((length = fis.read(bytes)) != -1) {
                respBuilder.setDataBlock(ByteString.copyFrom(bytes, 0, length));
                responseObserver.onNext(respBuilder.build());
                //模拟异常
                /*if (i++ == 3) {
                    Thread.sleep(2000);
                    throw new Exception("生产者模拟异常");
                }*/
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            responseObserver.onError(new StatusException(Status.INTERNAL));
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }

        }
        responseObserver.onCompleted();

    }

}

