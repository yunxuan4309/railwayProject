package com.homework.railwayproject.service.impl;

import com.homework.railwayproject.mapper.StationRoleMapper;
import com.homework.railwayproject.pojo.entity.Station;
import com.homework.railwayproject.pojo.entity.StationLevelCapacity;
import com.homework.railwayproject.service.StationCapacityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 站点容量服务实现类
 * 用于根据站点等级和设施情况计算站点容客量
 */
@Slf4j
@Service
public class StationCapacityServiceImpl implements StationCapacityService {
    
    @Autowired
    private StationRoleMapper stationRoleMapper;
    
    @Override
    public StationLevelCapacity getCapacityByStationLevel(String stationLevel) {
        return StationLevelCapacity.getByStationLevel(stationLevel);
    }
    
    @Override
    public Integer calculateCapacityByStation(Station station) {
        if (station == null) {
            return 0;
        }
        
        // 根据站点等级获取容量配置
        StationLevelCapacity capacityConfig = getCapacityByStationLevel(station.getStationLevel());
        
        // 使用容量配置计算实际容客量
        return capacityConfig.calculateActualCapacity(
            station.getPlatformCount(), 
            station.getGateCount()
        );
    }
    

    
    @Override
    public Integer getCapacityBySiteId(Integer siteId) {
        if (siteId == null) {
            return 0;
        }
        
        try {
            // 从数据库获取站点信息
            Station station = stationRoleMapper.getStationById(siteId);
            
            if (station == null) {
                log.warn("未找到站点ID为 {} 的站点信息", siteId);
                return 0;
            }
            
            // 计算站点实际容客量
            return calculateCapacityByStation(station);
        } catch (Exception e) {
            log.error("获取站点ID为 {} 的容客量时发生错误", siteId, e);
            return 0;
        }
    }
}