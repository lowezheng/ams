package com.apexsoft;

import com.apex.ams.server.AmsService;
import com.google.protobuf.ByteString;
import com.guoyuan.*;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

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
        return super.upload(responseObserver);
    }

    @Override
    public void download(NormalRequest request, StreamObserver<StreamResponse> responseObserver) {
        log.info("请求报文：{}", request.toString());
        InputStream fis = null;
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
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            responseObserver.onError(new Exception("文件下载异常:" + e.getMessage(), e));
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

    @Override
    public StreamObserver<StreamRequest> uploadAndDownload(StreamObserver<StreamResponse> responseObserver) {
        return super.uploadAndDownload(responseObserver);
    }
}

