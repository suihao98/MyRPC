package com.test.client;

import com.test.client.Impl.NettyRPCClient;
import com.test.entity.RPCRequest;
import com.test.entity.RPCResponse;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
@AllArgsConstructor
public class RPCClientProxy implements InvocationHandler {
    // 传入参数Service接口的class对象，反射封装成一个request
    @Resource
    private RPCClient rpcClient;

    RPCClientProxy(){
    }

    // jdk 动态代理，每一次代理对象调用方法，会经过此方法增强（反射获取request对象，socket发送至客户端）
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // request的构建，使用了lombok中的builder，代码简洁
        RPCRequest request = RPCRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsTypes(method.getParameterTypes())
                .build();
        // 数据传输
//        RPCResponse response = IOClient.sendRequest(host, port, request);
        RPCResponse response = rpcClient.sendRequest(request);
        return response.getData();
    }
    public <T>T getProxy(Class<T> clazz){
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T)o;
    }
}