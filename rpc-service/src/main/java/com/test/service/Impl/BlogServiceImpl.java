package com.test.service.Impl;

import com.test.annotation.RPCService;
import com.test.entity.Blog;
import com.test.service.BlogService;

@RPCService(serviceName = "com.test.service.BlogService")//自定义注解，服务注入
public class BlogServiceImpl implements BlogService {
    @Override
    public Blog getBlogById(Integer id) {
        Blog blog = Blog.builder().id(id).title("我的博客").useId(22).build();
        System.out.println("客户端查询了"+id+"博客");
        return blog;
    }
}
