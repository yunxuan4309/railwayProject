package com.homework.railwayproject.mapper;

import com.homework.railwayproject.pojo.dto.StationPeakHourStatDTO;
import com.homework.railwayproject.pojo.entity.PeakHourStat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 高峰时段统计Mapper接口
 */
@Mapper
public interface PeakHourStatMapper {
    
    /**
     * 按小时统计指定日期的客流数据
     * 
     * @param date 指定日期
     * @return 小时级客流统计数据列表
     */
    List<PeakHourStat> selectHourlyStatByDate(@Param("date") LocalDate date);
    
    /**
     * 根据站点ID获取该站点客流量最高的时段
     * 
     * @param stationId 站点ID
     * @return 站点最高客流时段统计
     */
    StationPeakHourStatDTO selectTopPeakHourByStationId(@Param("stationId") Integer stationId);
    
    /**
     * 根据站点ID和日期获取该站点客流量最高的时段
     * 
     * @param stationId 站点ID
     * @param date 指定日期
     * @return 站点最高客流时段统计
     */
    StationPeakHourStatDTO selectTopPeakHourByStationIdAndDate(@Param("stationId") Integer stationId, @Param("date") LocalDate date);
}