package com.homework.railwayproject.service;

import com.homework.railwayproject.pojo.entity.CapacityMatchingStat;

/**
 * 容量匹配度服务接口
 * 用于评估高峰客流与站台容量的匹配情况
 */
public interface CapacityMatchingService {
    
    /**
     * 计算单个站点的容量匹配度（根据站点ID自动获取站台容量）
     * 
     * @param siteId 站点ID
     * @param peakPassengerFlow 高峰期客流量
     * @return 容量匹配度统计信息
     */
    CapacityMatchingStat calculateCapacityMatchingBySiteId(Integer siteId, Integer peakPassengerFlow);
}