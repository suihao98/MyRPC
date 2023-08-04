package com.test.service.Impl;

import com.test.annotation.RPCService;
import com.test.entity.User;
import com.test.service.UserService;

import java.util.UUID;

@RPCService(serviceName = "com.test.service.UserService")//自定义注解，服务注入
public class UserServiceImpl implements UserService {
    @Override
    public User getUserByUserId(Integer id) {
        System.out.println("客户端查询了"+id+"的用户");
        // 模拟从数据库中取用户的行为
        User user = User.builder().userName(UUID.randomUUID().toString())
                .id(id)
                .sex("男").build();
        return user;
    }

    @Override
    public Integer insertUserId(User user) {
        System.out.println("插入数据成功："+user);
        return 1;
    }

    @Override
    public String hello() {
        return "Hello World!";
    }

}
