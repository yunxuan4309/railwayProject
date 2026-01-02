package com.homework.railwayproject.mapper;

import com.homework.railwayproject.pojo.dto.HistoricalPassengerFlowData;
import com.homework.railwayproject.pojo.entity.StationPassengerFlowStat;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StationPassengerFlowStatMapper {
    
    /**
     * 查询指定时间段内各站点的上客量和下客量前20名
     * 时间段为当天06:30到第二天06:30
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 站点客流统计列表
     */
    List<StationPassengerFlowStat> selectTop20Stations(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    /**
     * 查询指定站点在指定时间段内的客流统计
     * 
     * @param siteId 站点ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 站点客流统计数据
     */
    StationPassengerFlowStat getStationPassengerFlowByDate(@Param("siteId") Integer siteId,
                                                           @Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate);
                                                           
    /**
     * 获取指定站点的客流统计数据
     *
     * @param siteId 站点ID
     * @return 站点客流统计数据
     */
    StationPassengerFlowStat getStationPassengerFlow(Integer siteId);
    
    /**
     * 获取用于客流预测的历史数据
     * 
     * @param siteId 站点ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 历史数据列表，包含[时段, 上客量, 下客量, 日期]
     */
    List<HistoricalPassengerFlowData> getHistoricalDataForPrediction(@Param("siteId") Integer siteId,
                                                                     @Param("startDate") LocalDate startDate,
                                                                     @Param("endDate") LocalDate endDate);
}