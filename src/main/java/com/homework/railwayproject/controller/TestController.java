package com.homework.railwayproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db")
    public String testDb() {
        try {
            // 简单查询测试数据库连接
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "数据库连接成功: " + result;
        } catch (Exception e) {
            return "数据库连接失败: " + e.getMessage();
        }
    }

    @GetMapping("/ping")
    public String ping() {
        return "服务运行正常";
    }
}