package com.homework.railwayproject.pojo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 用于客流预测的历史数据传输对象
 */
@Data
public class HistoricalPassengerFlowData {
    /**
     * 时段
     */
    private LocalTime departTime;
    
    /**
     * 上客量
     */
    private Integer boardingCount;
    
    /**
     * 下客量
     */
    private Integer alightingCount;
    
    /**
     * 日期
     */
    private LocalDate travelDate;
}