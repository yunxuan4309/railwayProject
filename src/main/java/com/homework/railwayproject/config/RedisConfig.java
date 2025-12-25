package com.homework.railwayproject.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homework.railwayproject.pojo.entity.StationPassengerFlowStat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类，用于配置RedisTemplate的序列化方式
 * 解决Redis反序列化异常问题
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), 
                                          com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL);
        // 禁用FAIL_ON_UNKNOWN_PROPERTIES，以处理可能的序列化不一致问题
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 处理可能的字符编码问题
        objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.IGNORE_UNDEFINED, true);
        // 处理非法的Unicode字符
        objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.STRICT_DUPLICATE_DETECTION, false);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // 设置key和value的序列化规则
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

        // 设置hashKey和hashValue的序列化规则
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}