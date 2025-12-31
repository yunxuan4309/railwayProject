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
     * 从数据库获取，而不是使用默认值
     */
    private Double sensitivity;
}