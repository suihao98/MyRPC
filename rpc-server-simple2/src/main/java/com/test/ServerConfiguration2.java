package com.test;

import org.springframework.context.annotation.*;

@Configuration
@ComponentScans({
        @ComponentScan("com.test.register"),
        @ComponentScan("com.test.server"),
        @ComponentScan("com.test.server.Impl"),
})
@PropertySource(value = "classpath:rpc.properties", encoding = "UTF-8")
public class ServerConfiguration2 {
}
