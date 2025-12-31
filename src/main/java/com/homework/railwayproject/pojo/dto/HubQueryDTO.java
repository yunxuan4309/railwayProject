package com.homework.railwayproject.pojo.dto;

import lombok.Data;

/**
 * 枢纽识别查询参数DTO类
 * 用于接收枢纽分析的查询条件
 */
@Data
public class HubQueryDTO {

    /**
     * 网别
     * 支持值：
     *   - "高铁"：高速铁路（车次G开头）
     *   - "城际"：城际铁路（车次C开头）
     *   - "普速"：普速铁路（非G、非C开头）
     *   - null或不传：查询所有网别
     */
    private String trainType;

    /**
     * 返回TOP N个枢纽
     * 默认值：10
     * 示例：传5表示返回TOP5枢纽
     */
    private Integer topN;
}
