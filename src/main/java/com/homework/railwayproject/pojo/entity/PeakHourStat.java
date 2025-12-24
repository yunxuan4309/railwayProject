package com.homework.railwayproject.pojo.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * 高峰时段统计数据实体类
 */
@Data
public class PeakHourStat implements Serializable {
    
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
     * 是否高峰期
     */
    private Boolean isPeak;
    
    /**
     * 灵敏度调节参数（百分比）
     * 默认值为15%，即0.15
     */
    private Double sensitivity = 0.15;
}