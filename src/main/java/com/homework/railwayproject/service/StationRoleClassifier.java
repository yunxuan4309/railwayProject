package com.homework.railwayproject.service;

import com.homework.railwayproject.mapper.StationRoleMapper;
import com.homework.railwayproject.pojo.dto.StationStats;
import com.homework.railwayproject.pojo.entity.StationRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 站点角色分类器
 * 根据站点的客流特征自动给站点打标签
 */
@Slf4j
@Service
public class StationRoleClassifier {
    
    @Autowired
    private StationRoleMapper stationRoleMapper;

    /**
     * 根据指定日期的客流数据分析站点角色
     *
     * @param stationId     站点ID
     * @param analysisDate  分析日期
     * @return 站点角色
     */
    public StationRole classifyStation(Integer stationId, LocalDate analysisDate) {
        // 统计发送、到达、中转比例
        StationStats stats = calculateStationStats(stationId, analysisDate);
        
        // 分类规则
        if (stats.getDepartureRatio().doubleValue() >= 0.60) {
            return StationRole.DEPARTURE_STATION;
        } else if (stats.getArrivalRatio().doubleValue() >= 0.60) {
            return StationRole.ARRIVAL_STATION;
        } else if (stats.getTransferRatio().doubleValue() >= 0.40) {
            return StationRole.TRANSFER_STATION;
        } else {
            return StationRole.PASS_THROUGH_STATION;
        }
    }
    
    /**
     * 计算站点统计数据
     *
     * @param stationId    站点ID
     * @param analysisDate 分析日期
     * @return 站点统计数据
     */
    public StationStats calculateStationStats(Integer stationId, LocalDate analysisDate) {
        // 获取站点相关统计数据
        return stationRoleMapper.selectStationStats(stationId, analysisDate);
    }
}