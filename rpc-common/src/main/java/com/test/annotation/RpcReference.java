package com.test.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})//用于属性字段上
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {//服务依赖，类似于spring的@autowired理解

    public String referenceName();//根据name确认对象，类似于spring的@autowired的byName匹配模式，name要唯一
}
