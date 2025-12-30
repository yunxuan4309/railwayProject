package com.homework.railwayproject.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计结果DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResult {
    /**
     * 统计类型
     */
    private String type;
    
    /**
     * 统计值
     */
    private Long count;
    
    /**
     * 描述信息
     */
    private String description;
}