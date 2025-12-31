package com.homework.railwayproject.service.impl;

import com.homework.railwayproject.mapper.StationLevelCapacityMapper;
import com.homework.railwayproject.mapper.StationRoleMapper;
import com.homework.railwayproject.pojo.entity.SensitivityConfig;
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
    
    @Autowired
    private StationLevelCapacityMapper stationLevelCapacityMapper;
    
    @Override
    public StationLevelCapacity getCapacityByStationLevel(String stationLevel) {
        // 从数据库获取站点等级容量配置
        SensitivityConfig config = stationLevelCapacityMapper.selectByStationLevel(stationLevel);
        
        if (config != null && config.getSensitivityValue() != null) {
            // 从数据库配置中解析容量信息
            // sensitivityValue存储基础容量，description存储"站台容量,检票口容量"
            try {
                Integer baseCapacity = config.getSensitivityValue().intValue();
                String[] parts = config.getDescription().split(",");
                if (parts.length >= 2) {
                    Integer platformCapacity = Integer.parseInt(parts[0].trim());
                    Integer gateCapacity = Integer.parseInt(parts[1].trim());
                    
                    return new StationLevelCapacity(stationLevel, 
                        getLevelName(stationLevel), 
                        baseCapacity, 
                        platformCapacity, 
                        gateCapacity);
                }
            } catch (Exception e) {
                log.warn("解析数据库中站点等级容量配置失败，使用默认配置，站点等级: {}", stationLevel, e);
            }
        }
        
        // 如果数据库中无配置，则返回默认配置
        for (StationLevelCapacity defaultConfig : StationLevelCapacity.getDefaultConfigurations()) {
            if (defaultConfig.getStationLevel().equals(stationLevel)) {
                return defaultConfig;
            }
        }
        
        // 如果未找到对应等级，返回五等站配置作为默认值
        return new StationLevelCapacity("五等", "五等站", 500, 80, 60);
    }
    
    /**
     * 根据站点等级获取等级名称
     *
     * @param stationLevel 站点等级
     * @return 等级名称
     */
    private String getLevelName(String stationLevel) {
        switch (stationLevel) {
            case "特等":
                return "特等站";
            case "一等":
                return "一等站";
            case "二等":
                return "二等站";
            case "三等":
                return "三等站";
            case "四等":
                return "四等站";
            case "五等":
                return "五等站";
            default:
                return stationLevel + "站";
        }
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