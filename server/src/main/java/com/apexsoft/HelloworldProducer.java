package com.apexsoft;

import com.apex.ams.server.AmsService;
import com.guoyuan.helloworld.HelloRequest;
import com.guoyuan.helloworld.HelloResponse;
import com.guoyuan.helloworld.TestServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AmsService
public class HelloworldProducer extends TestServiceGrpc.TestServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(HelloworldProducer.class);

    @Override
    public void handle(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        log.info(request.toString());
        HelloResponse.Builder builder= HelloResponse.newBuilder();
        builder.setCode(1)
                .setNote("成功");
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}

