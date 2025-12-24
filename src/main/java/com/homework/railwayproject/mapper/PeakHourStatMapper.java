package com.homework.railwayproject.mapper;

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
}