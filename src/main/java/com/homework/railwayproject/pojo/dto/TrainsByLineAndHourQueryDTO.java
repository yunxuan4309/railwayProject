package com.homework.railwayproject.pojo.dto;

import lombok.Data;

/**
 * 根据线路和时段查询列车的查询条件DTO
 */
@Data
public class TrainsByLineAndHourQueryDTO {
    /**
     * 线路编码
     */
    private String lineCode;
    
    /**
     * 时段（小时）
     * 如：5 表示查询5点到6点之间的列车
     */
    private Integer hour;
}