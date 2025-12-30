package com.homework.railwayproject.service.impl;

import com.homework.railwayproject.mapper.PeakHourStatMapper;
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
        return peakHourStatMapper.selectHourlyStatByDate(date);
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
}