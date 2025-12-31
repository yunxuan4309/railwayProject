package com.homework.railwayproject.pojo.dto;

import lombok.Data;

import java.time.LocalTime;

/**
 * 站点高峰时段统计数据传输对象
 */
@Data
public class StationPeakHourStatDTO {
    
    /**
     * 时段开始时间
     */
    private LocalTime startTime;
    
    /**
     * 时段结束时间
     */
    private LocalTime endTime;
    
    /**
     * 客流量
     */
    private Integer passengerCount;
    
    /**
     * 站点ID
     */
    private Integer stationId;
    
    /**
     * 站点名称
     */
    private String stationName;
}