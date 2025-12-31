package com.homework.railwayproject.service;

import com.homework.railwayproject.pojo.dto.StationPeakHourStatDTO;
import com.homework.railwayproject.pojo.entity.PeakHourStat;

import java.time.LocalDate;
import java.util.List;

/**
 * 高峰时段统计服务接口
 */
public interface PeakHourStatService {
    
    /**
     * 获取指定日期的高峰时段统计信息
     * 
     * @param date 指定日期
     * @return 高峰时段统计列表
     */
    List<PeakHourStat> getPeakHours(LocalDate date);
    
    /**
     * 查找最拥挤的三个连续时段
     * 
     * @param date 指定日期
     * @return 最拥挤的三个连续时段列表
     */
    List<PeakHourStat> getTop3ConsecutivePeakHours(LocalDate date);
    
    /**
     * 查找最拥挤的三个连续时段（支持灵敏度调节）
     * 
     * @param date 指定日期
     * @param sensitivity 灵敏度调节参数（0.0-1.0之间）
     * @return 最拥挤的三个连续时段列表
     */
    List<PeakHourStat> getTop3ConsecutivePeakHours(LocalDate date, double sensitivity);
    
    /**
     * 根据站点ID和日期获取该站点客流量最高的时段
     * 
     * @param stationId 站点ID
     * @param date 指定日期
     * @return 站点最高客流时段统计
     */
    StationPeakHourStatDTO getTopPeakHourByStationId(Integer stationId, LocalDate date);

    /**
     * 根据站点ID和日期获取该站点所有时段的客流量
     * 
     * @param stationId 站点ID
     * @param date 指定日期
     * @return 站点所有时段客流统计列表
     */
    List<PeakHourStat> getHourlyStatByStationIdAndDate(Integer stationId, LocalDate date);
}