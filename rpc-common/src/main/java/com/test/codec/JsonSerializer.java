package com.test.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.entity.RPCRequest;
import com.test.entity.RPCResponse;

/**
 * 由于json序列化的方式是通过把对象转化成字符串，丢失了Data对象的类信息，所以deserialize需要
 * 了解对象对象的类信息，根据类信息把JsonObject -> 对应的对象
 */
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        byte[] bytes = JSONObject.toJSONBytes(obj);
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
        // 传输的消息分为request与response
        switch (messageType) {
            case 0 -> { // 当消息类型为request
                RPCRequest request = JSON.parseObject(bytes, RPCRequest.class);
                if (request.getParams()==null) return request;
                Object[] objects = new Object[request.getParams().length];
                // 把json字串转化成对应的对象， fastjson可以读出基本数据类型，不用转化
                for (int i = 0; i < objects.length; i++) {
                    Class<?> paramsType = request.getParamsTypes()[i];
                    // 如果是A.isAssignableFrom(B) 确定一个类(B)是不是继承来自于另一个父类(A)，
                    // 一个接口(A)是不是实现了另外一个接口(B)，或者两个类相同。
                    if (!paramsType.isAssignableFrom(request.getParams()[i].getClass())) {
                        // JSON对象–>Java对象： JSONObject.toJavaObject(JSON对象实例, Java对象.class);
                        objects[i] = JSONObject.toJavaObject((JSONObject) request.getParams()[i], request.getParamsTypes()[i]);
                    } else {
                        objects[i] = request.getParams()[i];
                    }

                }
                request.setParams(objects);
                obj = request;
            }
            case 1 -> { // 当消息类型为response
                RPCResponse response = JSON.parseObject(bytes, RPCResponse.class);
                Class<?> dataType = response.getDataType();
                if (!dataType.isAssignableFrom(response.getData().getClass())) {
                    response.setData(JSONObject.toJavaObject((JSONObject) response.getData(), dataType));
                }
                obj = response;
            }
            default -> {
                System.out.println("暂时不支持此种消息");
                throw new RuntimeException();
            }
        }
        return obj;
    }

    // 1 代表着json序列化方式
    @Override
    public int getType() {
        return 1;
    }
}