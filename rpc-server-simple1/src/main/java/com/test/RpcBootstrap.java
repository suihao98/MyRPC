package com.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RpcBootstrap {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(ServerConfiguration.class);
    }
}