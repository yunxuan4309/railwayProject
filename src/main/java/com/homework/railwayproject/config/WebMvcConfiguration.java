package com.homework.railwayproject.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//Author:[谢云轩]
//QQ:[1721476339]
//ID:[632307060623]
//Date:2025/11/19
//Time:13:49
@Slf4j
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer
{
    public WebMvcConfiguration(){
        log.debug("创建跨域配置对象:WebMvcConfigurer成功");
    }

    @Override

    public void addCorsMappings(CorsRegistry registry) {
        /*maxAge(3600)探测有效期是3600毫秒
         * allowCredentials(true)既允许使用token,
         * allowedOriginPatterns("*")可以允许任何形式的头文件访问*/


        registry.addMapping("/**").allowedHeaders("*").allowedMethods("*")
                .allowedOriginPatterns("*").allowCredentials(true).maxAge(3600);
    }


}