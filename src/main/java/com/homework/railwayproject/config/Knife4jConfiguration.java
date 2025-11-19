package com.homework.railwayproject.config;

//Author:[谢云轩]
//QQ:[1721476339]
//ID:[632307060623]
//Date:2025/11/18
//Time:21:44

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfiguration {

    /**
     * 访问路径:http//localhost:9080/doc.html
     * 【重要】指定Controller包路径,这样前端的伙伴就知道
     */
    private String basePackage = "com.homework.railwayproject.controller";/*对controller下的东西起作用,主要起作用的*/
    /**
     * 分组名称
     */
    private String groupName = "homework";
    /**
     * 标题
     */
    private String title = "高铁客运量数据分析系统";
    /**
     * 简介
     */
    private String description = "高铁客运量数据分析";
    /**
     * 服务条款URL
     */
    private String termsOfServiceUrl = "http://www.baidu.com";
    /**
     * 联系人
     */
    private String contactName = "谢云轩";
    /**
     * 联系网址
     */
    private String contactUrl = "http://www.baidu.com";
    /**
     * 联系邮箱
     */
    private String contactEmail = "1721476339@qq.com";
    /**
     * 版本号
     */
    private String version = "1.0.0";


    /**
     * 指定Controller包路径
     */

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("railway-project") // 分组名称
                .packagesToScan(basePackage)
                .pathsToMatch("/**") // 匹配所有路径
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("铁路项目在线API文档")
                        .description("铁路项目在线API文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("开发团队")
                                .url("http://your-domain.com")
                                .email("your-email@example.com"))
                        .termsOfService("http://www.apache.org/licenses/LICENSE-2.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0")));
    }
}