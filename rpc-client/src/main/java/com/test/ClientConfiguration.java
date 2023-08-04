package com.test;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScans({
        @ComponentScan("com.test.entity"),
        @ComponentScan("com.test.register"),
        @ComponentScan("com.test.client.Impl"),
        @ComponentScan("com.test.client"),
})
@PropertySource(value = "classpath:rpc.properties", encoding = "UTF-8")
public class ClientConfiguration {

}
