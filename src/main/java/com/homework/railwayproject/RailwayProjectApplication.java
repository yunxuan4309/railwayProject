package com.homework.railwayproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author 17214
 */
@SpringBootApplication
@MapperScan("com.homework.railwayproject.mapper")
public class RailwayProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(RailwayProjectApplication.class, args);
    }

}
