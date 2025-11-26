package com.homework.railwayproject.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Author:[谢云轩]
//QQ:[1721476339]
//ID:[632307060623]
//Date:2025/11/19
//Time:13:46
@Slf4j
@Configuration
public class ValidationConfiguration {

    public ValidationConfiguration() {
        log.debug("创建配置类对象：ValidationConfiguration");
    }
    /*Validation 配置快速失败:当某一个参数错误或者失败时候,不再对其他参数进行校验*/
    @Bean/*自动生成对象,名字叫方法名称validator*/
    public Validator validator() {
        return Validation.byProvider(HibernateValidator.class)
                .configure() // 开始配置
                .failFast(true) // 配置快速失败机制,如果有一个失败了,后面的错误不再校验
                .buildValidatorFactory() // 构建Validator工厂
                .getValidator(); // 从Validator工厂中获取Validator对象
    }
}