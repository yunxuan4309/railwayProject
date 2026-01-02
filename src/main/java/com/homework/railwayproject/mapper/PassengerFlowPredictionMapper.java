package com.homework.railwayproject.mapper;

import com.homework.railwayproject.pojo.entity.PassengerFlowPrediction;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PassengerFlowPredictionMapper {
    
    /**
     * 插入客流预测数据
     * 
     * @param prediction 客流预测实体
     * @return 影响的行数
     */
    int insertPrediction(PassengerFlowPrediction prediction);
    
    /**
     * 根据站点ID和日期范围查询客流预测数据
     * 
     * @param siteId 站点ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 客流预测数据列表
     */
    List<PassengerFlowPrediction> selectPredictionsBySiteAndDateRange(
            @Param("siteId") Integer siteId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * 根据站点ID查询最新的客流预测数据
     * 
     * @param siteId 站点ID
     * @return 最新的客流预测数据
     */
    List<PassengerFlowPrediction> selectLatestPredictionsBySite(@Param("siteId") Integer siteId);
    
    /**
     * 根据日期查询客流预测数据
     * 
     * @param predictionDate 预测日期
     * @return 客流预测数据列表
     */
    List<PassengerFlowPrediction> selectPredictionsByDate(@Param("predictionDate") LocalDate predictionDate);
}