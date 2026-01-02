package com.homework.railwayproject.pojo.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 客流预测实体类
 */
@Data
public class PassengerFlowPrediction {
    /**
     * 预测ID
     */
    private Integer predictionId;
    
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
    
    /**
     * 创建时间
     */
    private java.util.Date createTime;
}