package com.homework.railwayproject.pojo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 客流预测数据传输对象
 */
@Data
public class PassengerFlowPredictionDTO {
    /**
     * 站点ID
     */
    private Integer siteId;
    
    /**
     * 站点名称
     */
    private String siteName;
    
    /**
     * 预测日期
     */
    private LocalDate predictionDate;
    
    /**
     * 预测时段开始时间
     */
    private LocalTime predictionTime;
    
    /**
     * 预测的上客量
     */
    private Integer predictedBoardingCount;
    
    /**
     * 预测的下客量
     */
    private Integer predictedAlightingCount;
    
    /**
     * 预测的总客流量
     */
    private Integer predictedTotalFlow;
    
    /**
     * 预测方法
     */
    private String predictionMethod;
    
    /**
     * 预测准确度评估
     */
    private Double accuracy;
}