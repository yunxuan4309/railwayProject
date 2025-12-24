package com.homework.railwayproject.service;

import com.homework.railwayproject.pojo.entity.StationPassengerFlowStat;

import java.util.List;

/**
 * 站点客流统计服务接口
 */
public interface StationPassengerFlowStatService {
    
    /**
     * 获取站点客流统计前20名
     * 
     * @return 站点客流统计列表
     */
    List<StationPassengerFlowStat> getTop20Stations();
    
    /**
     * 定时任务：统计并缓存站点客流数据
     */
    void calculateAndCacheTop20Stations();

    StationPassengerFlowStat getStationPassengerFlow(Integer siteId);
}