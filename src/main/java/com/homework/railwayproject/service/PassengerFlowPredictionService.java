package com.homework.railwayproject.service;

import com.homework.railwayproject.pojo.dto.PassengerFlowPredictionDTO;
import com.homework.railwayproject.pojo.entity.PassengerFlowPrediction;

import java.time.LocalDate;
import java.util.List;

public interface PassengerFlowPredictionService {
    
    /**
     * 基于历史数据生成短期客流预测（使用移动平均算法）
     * 
     * @param siteId 站点ID
     * @param predictionDate 预测日期
     * @param daysForPrediction 用于预测的历史天数
     * @return 客流预测数据列表
     */
    List<PassengerFlowPredictionDTO> predictPassengerFlowByMovingAverage(
            Integer siteId, 
            LocalDate predictionDate, 
            int daysForPrediction);
    
    /**
     * 基于历史数据生成短期客流预测（使用周期性分析算法）
     * 
     * @param siteId 站点ID
     * @param predictionDate 预测日期
     * @return 客流预测数据列表
     */
    List<PassengerFlowPredictionDTO> predictPassengerFlowByPeriodicity(
            Integer siteId, 
            LocalDate predictionDate);
    
    /**
     * 保存客流预测结果到数据库
     * 
     * @param predictions 预测结果列表
     */
    void savePredictions(List<PassengerFlowPrediction> predictions);
    
    /**
     * 将预测DTO结果转换为实体并保存到数据库
     * 
     * @param predictionDtos 预测DTO列表
     * @param predictionMethod 预测方法
     */
    void savePredictionDTOs(List<PassengerFlowPredictionDTO> predictionDtos, String predictionMethod);
    
    /**
     * 根据站点ID和日期范围获取历史客流数据用于预测
     * 
     * @param siteId 站点ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 历史客流数据列表
     */
    List<Object[]> getHistoricalPassengerFlowData(
            Integer siteId, 
            LocalDate startDate, 
            LocalDate endDate);
    
    /**
     * 根据站点ID和日期范围查询已有的预测数据
     * 
     * @param siteId 站点ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预测数据列表
     */
    List<PassengerFlowPrediction> getPredictionsBySiteAndDateRange(
            Integer siteId, 
            LocalDate startDate, 
            LocalDate endDate);
}