package com.test.server;

import com.test.annotation.RPCService;
import com.test.register.ServiceRegister;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 之前这里使用Map简单实现的
 * 存放服务接口名与服务端对应的实现类
 * 服务启动时要暴露其相关的实现类0
 * 根据request中的interface调用服务端中相关实现类
 */

@Component
public class ServiceProvider {
    /**
     * 一个实现类可能实现多个接口
     */
    private Map<String, Object> interfaceProvider;
    @Resource
    private ServiceRegister serviceRegister;

    @Value("${rpc.service_host}")
    private String host;
    @Value("${rpc.service_port}")
    private int port;

    public ServiceProvider(){
        this.interfaceProvider = new HashMap<>();
    }

    @PostConstruct
    public void init(){
        try{
            Reflections reflections = new Reflections("com.test.service");//创建一个Reflections 对象，并指定要扫描的包路径
            Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(RPCService.class);//该方法会扫描指定包路径下带有@RpcService注解的类的Class对象，并封装成一个set集合
            typesAnnotatedWith.stream().forEach(aClass -> {
                if (aClass.isAnnotationPresent(RPCService.class)) {
                    RPCService annotation = aClass.getAnnotation(RPCService.class);
                    try {
                        Object o = aClass.getDeclaredConstructor().newInstance();//遍历set集合，通过反射创建Class的对象
                        // 本机的映射表
                        interfaceProvider.put(annotation.serviceName(), o);//以@RpcService的name属性为key放到Bean容器中
                        // 在注册中心注册服务
                        serviceRegister.register(annotation.serviceName(), new InetSocketAddress(host, port));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void provideServiceInterface(Object service) {
        Class<?>[] interfaces = service.getClass().getInterfaces();

        for (Class clazz : interfaces) {
            // 本机的映射表 key:接口  value:实现类
            interfaceProvider.put(clazz.getName(), service);
//            System.out.println("key: "+clazz.getName()+", value:"+service);
            // 在注册中心注册服务
            serviceRegister.register(clazz.getName(), new InetSocketAddress(host, port));
        }

    }

    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }

    public int getPort() {
        return port;
    }
}
