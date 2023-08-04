package com.test.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.test.entity.RPCRequest;
import com.test.entity.RPCResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * kryo实现序列化和反序列化接口
 * kryo不是线程安全的，需要注意，使用独立线程实现
 */
public class KryoSerializer implements Serializer {
    // kryo 是非线程安全类
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RPCRequest.class);
        kryo.register(RPCResponse.class);
        // 默认值为true，是否关闭注册行为，关闭之后可能存在序列化问题，一般推荐设置为true
        kryo.setReferences(true);
        // 默认值为false，是否关闭循环引用，可以提高性能，但是一般不推荐设置为true
        kryo.setRegistrationRequired(false);

        // kryo.register(Object.class, new BeanSerializer(kryo, Object.class));
        return kryo;
    });


    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream))
        {
            Kryo kryo = kryoThreadLocal.get();
            // Object -> byte[]：将对象序列化为byte数组
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             Input input = new Input(bis)){
            Kryo kryo = kryoThreadLocal.get();
            switch (messageType) {
                case 0 -> { // 当消息类型为request
                    RPCRequest request = kryo.readObject(input, RPCRequest.class);
                    if(request.getParams() == null) return request;
                    Object[] objects = new Object[request.getParams().length];
                    System.arraycopy(request.getParams(), 0, objects, 0, objects.length);
                    request.setParams(objects);
                    obj = request;
                    kryoThreadLocal.remove();
                }
                case 1 -> { // 当消息类型为response
                    RPCResponse response = kryo.readObjectOrNull(input, RPCResponse.class);
                    obj = response;
                    kryoThreadLocal.remove();
                }
                default -> {
                    System.out.println("暂时不支持此种消息");
                    throw new RuntimeException();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    @Override
    public int getType() {
        return 2;
    }
}



