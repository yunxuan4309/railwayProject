package com.homework.railwayproject.service;

import com.homework.railwayproject.pojo.entity.StationPassengerFlowStat;

import java.time.LocalDate;
import java.util.List;

public interface StationPassengerFlowStatService {
    
    /**
     * 获取站点客流统计前20名
     * 
     * @return 站点客流统计列表
     */
    List<StationPassengerFlowStat> getTop20Stations();

    /**
     * 根据指定日期范围获取站点客流统计前20名
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 站点客流统计列表
     */
    List<StationPassengerFlowStat> getTop20StationsByDate(LocalDate startDate, LocalDate endDate);
    
    /**
     * 定时任务：统计并缓存站点客流数据
     */
    void calculateAndCacheTop20Stations();

    /**
     * 获取指定站点的客流统计数据
     * 
     * @param siteId 站点ID
     * @return 站点客流统计数据
     */
    StationPassengerFlowStat getStationPassengerFlow(Integer siteId);

    /**
     * 获取指定站点在指定日期范围内的客流统计数据
     * 
     * @param siteId 站点ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 站点客流统计数据
     */
    StationPassengerFlowStat getStationPassengerFlowByDate(Integer siteId, LocalDate startDate, LocalDate endDate);
}