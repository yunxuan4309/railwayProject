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
    private String basePackage = "com.cooxiao.jsd23104.controller";/*对controller下的东西起作用,主要起作用的*/
    /**
     * 分组名称
     */
    private String groupName = "jsd23104";
    /**
     * 主机名
     */
    private String host = "http://java.cooxiao.cn";
    /**
     * 标题
     */
    private String title = "酷鲨商城在线API文档--商品管理";
    /**
     * 简介
     */
    private String description = "酷鲨商城在线API文档--商品管理";
    /**
     * 服务条款URL
     */
    private String termsOfServiceUrl = "http://www.apache.org/licenses/LICENSE-2.0";
    /**
     * 联系人
     */
    private String contactName = "Java教学研发部";
    /**
     * 联系网址
     */
    private String contactUrl = "http://java.cooxiao.cn";
    /**
     * 联系邮箱
     */
    private String contactEmail = "java@cooxiao.cn";
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