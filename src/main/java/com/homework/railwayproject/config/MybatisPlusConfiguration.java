package com.homework.railwayproject.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Author:[谢云轩]
//QQ:[1721476339]
//ID:[632307060623]
//Date:2025/11/19
//Time:13:45
@Slf4j
@Configuration
// 注意：MyBatis-Plus 不需要 @MapperScan，它有自动配置
public class MybatisPlusConfiguration {

    public MybatisPlusConfiguration() {
        log.debug("创建配置类对象: MybatisPlusConfiguration");
    }

    /**
     * MyBatis-Plus 拦截器配置
     * 添加分页插件等
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        // 你可以在这里添加其他插件，比如乐观锁插件等

        return interceptor;
    }

    /**
     * 如果你还需要自定义的拦截器（如插入更新时间拦截器）
     * 可以继续保留，但需要适配 MyBatis-Plus
     */
    // @Bean
    // public InsertUpdateTimeInterceptor insertUpdateTimeInterceptor() {
    //     return new InsertUpdateTimeInterceptor();
    // }
}