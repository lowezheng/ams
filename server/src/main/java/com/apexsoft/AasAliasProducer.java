package com.apexsoft;

import com.alibaba.fastjson.JSONObject;
import com.apex.ams.common.CommRequest;
import com.apex.ams.common.CommResponse;
import com.apex.ams.common.CommonGrpc;
import com.apex.ams.server.AmsService;
import com.apex.ams.util.AmsMessageUtils;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AmsService(namespace = "com.guoyuan",alias="alias.test@com.guoyuan")
public class AasAliasProducer  extends CommonGrpc.CommonImplBase {
    @Override
    public void handle(CommRequest request, StreamObserver<CommResponse> responseObserver) {
        JSONObject param = AmsMessageUtils.toJSONObject(request.getParamMap());

        String name = param.getString("name");

        CommResponse.Builder builder = CommResponse.newBuilder();

        builder.setCode(AmsMessageUtils.CODE_SUCCESS);

        Map<String, Object> data = new HashMap<>();
        data.put("param1", 11);
        data.put("user",new User(){{
            setAge(1);
            setName("dddd");
        }});
        builder.putAllData(AmsMessageUtils.toAnyMap(data));
        List<User> users =new ArrayList<>();
        users.add(new User(){{
            setAge(1);
            setName("11111");
        }});
        users.add(new User(){{
            setAge(2);
            setName("22222");
        }});
        builder.addAllRecord(AmsMessageUtils.toCommDataList(users));

        responseObserver.onNext(builder.build());

        responseObserver.onCompleted();
    }
}