package com.homework.railwayproject.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 站点统计数据DTO
 */
@Data
public class StationStats {
    /**
     * 发送人数
     */
    private Integer departureCount;
    
    /**
     * 到达人数
     */
    private Integer arrivalCount;
    
    /**
     * 中转人数
     */
    private Integer transferCount;
    
    /**
     * 总客流量
     */
    private Integer totalPassengerFlow;
    
    /**
     * 发送比例
     */
    private BigDecimal departureRatio;
    
    /**
     * 到达比例
     */
    private BigDecimal arrivalRatio;
    
    /**
     * 中转比例
     */
    private BigDecimal transferRatio;
}