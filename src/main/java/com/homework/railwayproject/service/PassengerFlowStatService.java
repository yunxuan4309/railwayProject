package com.homework.railwayproject.service;

import com.homework.railwayproject.pojo.entity.PassengerFlowStat;

import java.time.LocalDate;
import java.util.List;

/**
 * 客流统计服务接口
 */
public interface PassengerFlowStatService {

    /**
     * 获取指定日期范围内的日客流统计数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 客流统计数据列表
     */
    List<PassengerFlowStat> getDailyStat(LocalDate startDate, LocalDate endDate);

    /**
     * 获取指定日期范围内的周客流统计数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 客流统计数据列表
     */
    List<PassengerFlowStat> getWeeklyStat(LocalDate startDate, LocalDate endDate);

    /**
     * 获取指定日期范围内的月客流统计数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 客流统计数据列表
     */
    List<PassengerFlowStat> getMonthlyStat(LocalDate startDate, LocalDate endDate);

    /**
     * 计算环比增长率
     *
     * @param stats 客流统计数据列表
     * @return 包含环比增长率的数据列表
     */
    List<PassengerFlowStat> calculateRingGrowthRate(List<PassengerFlowStat> stats);
    /**
     * 计算同比增长率
     *
     * @param stats 客流统计数据列表
     * @return 包含同比增长率的数据列表
     */
    List<PassengerFlowStat> calculateYearOnYearGrowthRate(List<PassengerFlowStat> stats);

    /**
     * 标记节假日
     *
     * @param stats 客流统计数据列表
     * @return 标记节假日后的数据列表
     */
    List<PassengerFlowStat> markHolidays(List<PassengerFlowStat> stats);
}