package com.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RpcBootstrap2 {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(ServerConfiguration2.class);
    }
}