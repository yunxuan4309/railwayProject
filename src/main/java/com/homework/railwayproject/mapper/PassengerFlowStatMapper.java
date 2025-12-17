package com.homework.railwayproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homework.railwayproject.pojo.entity.PassengerFlowStat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 客流统计数据Mapper接口
 */
@Mapper
public interface PassengerFlowStatMapper extends BaseMapper<PassengerFlowStat> {

    /**
     * 获取指定日期范围内的日客流统计数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 客流统计数据列表
     */
    List<PassengerFlowStat> selectDailyStat(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 获取指定日期范围内的周客流统计数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 客流统计数据列表
     */
    List<PassengerFlowStat> selectWeeklyStat(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 获取指定日期范围内的月客流统计数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 客流统计数据列表
     */
    List<PassengerFlowStat> selectMonthlyStat(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}