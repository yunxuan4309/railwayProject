package com.homework.railwayproject.service.impl;

import com.homework.railwayproject.mapper.StationRoleMapper;
import com.homework.railwayproject.pojo.entity.CapacityMatchingStat;
import com.homework.railwayproject.pojo.entity.CapacityMatchingStatus;
import com.homework.railwayproject.service.CapacityMatchingService;
import com.homework.railwayproject.service.StationCapacityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 容量匹配度服务实现类
 */
@Slf4j
@Service
public class CapacityMatchingServiceImpl implements CapacityMatchingService {
    
    @Autowired
    private StationRoleMapper stationRoleMapper;
    
    @Autowired
    private StationCapacityService stationCapacityService;
    
    /**
     * 计算单个站点的容量匹配度（根据站点ID自动获取站台容量）
     * 
     * @param siteId 站点ID
     * @param peakPassengerFlow 高峰期客流量
     * @return 容量匹配度统计信息
     */
    @Override
    public CapacityMatchingStat calculateCapacityMatchingBySiteId(Integer siteId, Integer peakPassengerFlow) {
        if (siteId == null) {
            throw new IllegalArgumentException("站点ID不能为空");
        }
        
        // 根据站点ID获取站台容量
        Integer platformCapacity = stationCapacityService.getCapacityBySiteId(siteId);
        
        if (platformCapacity == null || platformCapacity <= 0) {
            throw new IllegalArgumentException("无法获取站点ID为 " + siteId + " 的站台容量");
        }
        
        // 计算匹配度百分比
        double matchingPercentage = (double) peakPassengerFlow / platformCapacity * 100;
        
        // 确定匹配度状态
        CapacityMatchingStatus capacityMatchingStatus = CapacityMatchingStatus.getStatusByPercentage(matchingPercentage);
        
        // 创建容量匹配度统计对象
        CapacityMatchingStat stat = new CapacityMatchingStat();
        stat.setSiteId(siteId);
        stat.setPeakPassengerFlow(peakPassengerFlow);
        stat.setPlatformCapacity(platformCapacity);
        stat.setMatchingPercentage(matchingPercentage);
        stat.setMatchingStatus(capacityMatchingStatus.getCode());
        stat.setStatusDescription(capacityMatchingStatus.getDescription());
        stat.setCapacityMatchingStatus(capacityMatchingStatus);
        
        // 查询站点名称
        try {
            String siteName = stationRoleMapper.getStationNameById(siteId);
            stat.setSiteName(siteName != null ? siteName : "未知站点");
        } catch (Exception e) {
            log.warn("查询站点名称失败，站点ID: {}", siteId, e);
            stat.setSiteName("未知站点");
        }
        
        return stat;
    }
}