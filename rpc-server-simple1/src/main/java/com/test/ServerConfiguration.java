package com.test;

import org.springframework.context.annotation.*;

//@EnableAspectJAutoProxy  //开启注解实现AOP
@Configuration
@ComponentScans({
        @ComponentScan("com.test.register"),
        @ComponentScan("com.test.server"),
        @ComponentScan("com.test.server.Impl"),
})
@PropertySource(value = "classpath:rpc.properties", encoding = "UTF-8")
public class ServerConfiguration {
}
