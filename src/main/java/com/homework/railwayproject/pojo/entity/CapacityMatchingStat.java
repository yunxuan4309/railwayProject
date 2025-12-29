package com.homework.railwayproject.pojo.entity;

import lombok.Data;

/**
 * 容量匹配度统计实体类
 */
@Data
public class CapacityMatchingStat {
    /**
     * 站点ID
     */
    private Integer siteId;
    
    /**
     * 站点名称
     */
    private String siteName;
    
    /**
     * 高峰期客流量
     */
    private Integer peakPassengerFlow;
    
    /**
     * 站台容量
     */
    private Integer platformCapacity;
    
    /**
     * 匹配度百分比 (高峰客流/站台容量 * 100)
     */
    private Double matchingPercentage;
    
    /**
     * 匹配度状态：GREEN(绿灯: < 70%), YELLOW(黄灯: 70%-90%), RED(红灯: > 90%)
     */
    private String matchingStatus;
    
    /**
     * 状态描述
     */
    private String statusDescription;
    
    /**
     * 匹配度状态枚举
     */
    private CapacityMatchingStatus capacityMatchingStatus;
}