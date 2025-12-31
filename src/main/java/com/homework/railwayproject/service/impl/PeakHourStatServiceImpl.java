package com.homework.railwayproject.service.impl;

import com.homework.railwayproject.mapper.PeakHourStatMapper;
import com.homework.railwayproject.pojo.dto.StationPeakHourStatDTO;
import com.homework.railwayproject.pojo.entity.PeakHourStat;
import com.homework.railwayproject.service.PeakHourStatService;
import com.homework.railwayproject.service.SensitivityConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 高峰时段统计服务实现类
 */
@Slf4j
@Service
public class PeakHourStatServiceImpl implements PeakHourStatService {
    
    @Autowired
    private PeakHourStatMapper peakHourStatMapper;
    
    @Autowired
    private SensitivityConfigService sensitivityConfigService;
    
    @Override
    public List<PeakHourStat> getPeakHours(LocalDate date) {
        List<PeakHourStat> hourlyStats = peakHourStatMapper.selectHourlyStatByDate(date);
        
        if (hourlyStats == null || hourlyStats.isEmpty()) {
            return hourlyStats;
        }
        
        // 获取灵敏度配置值
        Double sensitivity = sensitivityConfigService.getPeakHourSensitivity();
        
        // 计算所有时段的平均客流量
        int totalPassengerCount = 0;
        for (PeakHourStat stat : hourlyStats) {
            totalPassengerCount += stat.getPassengerCount();
        }
        double averagePassengerCount = (double) totalPassengerCount / hourlyStats.size();
        
        // 根据灵敏度配置判断高峰时段
        // 高峰时段判断逻辑：客流量 > 平均客流量 * (0.5 + 0.5 * 灵敏度值)
        // 当灵敏度为0.0时，阈值为平均值的0.5倍（低阈值，更多时段被标记为高峰）
        // 当灵敏度为1.0时，阈值为平均值的1.0倍（高阈值，更严格的高峰判断）
        double peakThreshold = averagePassengerCount * (0.5 + 0.5 * sensitivity);
        
        for (PeakHourStat stat : hourlyStats) {
            // 设置灵敏度值
            stat.setSensitivity(sensitivity);
            // 根据客流量是否超过阈值来判断是否为高峰时段
            stat.setIsPeak(stat.getPassengerCount() > peakThreshold);
        }
        
        return hourlyStats;
    }
    
    @Override
    public List<PeakHourStat> getTop3ConsecutivePeakHours(LocalDate date) {
        Double defaultSensitivity = sensitivityConfigService.getPeakHourSensitivity();
        return getTop3ConsecutivePeakHours(date, defaultSensitivity); // 使用数据库中的默认灵敏度
    }
    
    /**
     * 查找最拥挤的三个连续时段（支持灵敏度调节）
     * 
     * @param date 指定日期
     * @param sensitivity 灵敏度调节参数（0.0-1.0之间）
     * @return 最拥挤的三个连续时段列表
     */
    public List<PeakHourStat> getTop3ConsecutivePeakHours(LocalDate date, double sensitivity) {
        // 获取按小时统计的数据
        List<PeakHourStat> hourlyStats = peakHourStatMapper.selectHourlyStatByDate(date);
        
        if (hourlyStats == null || hourlyStats.size() < 3) {
            return new ArrayList<>();
        }
        
        List<PeakHourStat> top3Peaks = new ArrayList<>();
        int maxSum = -1;
        
        // 查找客流量最大的三个连续时段
        for (int i = 0; i <= hourlyStats.size() - 3; i++) {
            int sum = 0;
            for (int j = 0; j < 3; j++) {
                sum += hourlyStats.get(i + j).getPassengerCount();
            }
            
            if (sum > maxSum) {
                maxSum = sum;
                top3Peaks.clear();
                for (int j = 0; j < 3; j++) {
                    PeakHourStat stat = hourlyStats.get(i + j);
                    stat.setSensitivity(sensitivity);
                    top3Peaks.add(stat);
                }
            }
        }
        
        // 标记为高峰期
        for (PeakHourStat stat : top3Peaks) {
            stat.setIsPeak(true);
        }
        
        return top3Peaks;
    }
    
    @Override
    public StationPeakHourStatDTO getTopPeakHourByStationId(Integer stationId, LocalDate date) {
        return peakHourStatMapper.selectTopPeakHourByStationIdAndDate(stationId, date);
    }

    @Override
    public List<PeakHourStat> getHourlyStatByStationIdAndDate(Integer stationId, LocalDate date) {
        List<PeakHourStat> hourlyStats = peakHourStatMapper.selectHourlyStatByStationIdAndDate(stationId, date);
        
        if (hourlyStats == null || hourlyStats.isEmpty()) {
            return hourlyStats;
        }
        
        // 获取灵敏度配置值
        Double sensitivity = sensitivityConfigService.getPeakHourSensitivity();
        
        // 计算所有时段的平均客流量
        int totalPassengerCount = 0;
        for (PeakHourStat stat : hourlyStats) {
            totalPassengerCount += stat.getPassengerCount();
        }
        double averagePassengerCount = (double) totalPassengerCount / hourlyStats.size();
        
        // 根据灵敏度配置判断高峰时段
        // 高峰时段判断逻辑：客流量 > 平均客流量 * (0.5 + 0.5 * 灵敏度值)
        // 当灵敏度为0.0时，阈值为平均值的0.5倍（低阈值，更多时段被标记为高峰）
        // 当灵敏度为1.0时，阈值为平均值的1.0倍（高阈值，更严格的高峰判断）
        double peakThreshold = averagePassengerCount * (0.5 + 0.5 * sensitivity);
        
        for (PeakHourStat stat : hourlyStats) {
            // 设置灵敏度值
            stat.setSensitivity(sensitivity);
            // 根据客流量是否超过阈值来判断是否为高峰时段
            stat.setIsPeak(stat.getPassengerCount() > peakThreshold);
        }
        
        return hourlyStats;
    }
}