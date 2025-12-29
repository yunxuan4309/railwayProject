package com.homework.railwayproject.service;

import com.homework.railwayproject.pojo.entity.Station;
import com.homework.railwayproject.pojo.entity.StationLevelCapacity;

/**
 * 站点容量服务接口
 * 用于根据站点等级和设施情况计算站点容客量
 */
public interface StationCapacityService {
    
    /**
     * 根据站点等级获取容客量配置
     * 
     * @param stationLevel 站点等级
     * @return 站点等级容量配置
     */
    StationLevelCapacity getCapacityByStationLevel(String stationLevel);
    
    /**
     * 根据站点信息计算实际容客量
     * 
     * @param station 站点信息
     * @return 实际容客量
     */
    Integer calculateCapacityByStation(Station station);

    /**
     * 根据站点ID获取站点实际容客量
     * 
     * @param siteId 站点ID
     * @return 站点实际容客量
     */
    Integer getCapacityBySiteId(Integer siteId);
}