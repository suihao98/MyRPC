package com.test.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RPCService {//服务注入，此注解修饰的类，会在netty服务端启动时加载到Bean容器中

    public String serviceName();//唯一，根据name确认对象。类似于spring的@autowired的byName匹配模式，name要唯一
}
