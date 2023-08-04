package com.test;

import com.test.client.RPCClientProxy;
import com.test.entity.Blog;
import com.test.entity.User;
import com.test.service.BlogService;
import com.test.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class StartClient {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ClientConfiguration.class);
        RPCClientProxy rpcClientProxy = applicationContext.getBean(RPCClientProxy.class);

        // 代理客户端根据不同的服务，获得一个代理类， 并且这个代理类的方法以或者增强（封装数据，发送请求）
        UserService userService = rpcClientProxy.getProxy(UserService.class);
        BlogService blogService = rpcClientProxy.getProxy(BlogService.class);

        // 调用方法
        User userByUserId = userService.getUserByUserId(10);
        System.out.println("从服务端得到的user为：" + userByUserId);

        User user = User.builder().userName("张三").id(100).sex("男").build();
        Integer integer = userService.insertUserId(user);
        System.out.println("向服务端插入数据：" + integer);

        Blog blogById = blogService.getBlogById(10000);
        System.out.println("从服务端得到的blog为：" + blogById);

        System.out.println(userService.hello());
    }
}