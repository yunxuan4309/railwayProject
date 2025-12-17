package com.homework.railwayproject.controller;

import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.pojo.entity.PassengerFlowStat;
import com.homework.railwayproject.service.PassengerFlowStatService;
import com.homework.railwayproject.web.JsonResult;
import com.homework.railwayproject.web.ServiceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 客流统计控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/statistics")
public class PassengerFlowStatController {

    @Autowired
    private PassengerFlowStatService passengerFlowStatService;

    /**
     * 获取日客流统计数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 客流统计数据列表
     */
    @GetMapping("/daily")
    public JsonResult<List<PassengerFlowStat>> getDailyStat(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            List<PassengerFlowStat> stats = passengerFlowStatService.getDailyStat(startDate, endDate);
            stats = passengerFlowStatService.calculateRingGrowthRate(stats);
            stats = passengerFlowStatService.calculateYearOnYearGrowthRate(stats);
            stats = passengerFlowStatService.markHolidays(stats);
            return JsonResult.ok(stats);
        } catch (Exception e) {
            log.error("获取日客流统计数据失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    /**
     * 获取周客流统计数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 客流统计数据列表
     */
    @GetMapping("/weekly")
    public JsonResult<List<PassengerFlowStat>> getWeeklyStat(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            //传入startDate和endDate，求starData的所在周的周一，endData的所在周的周日
            LocalDate monday = startDate.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            LocalDate sunday = endDate.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
            System.out.println(
                    "startDate: " + monday +
                            " endDate: " + sunday
            );
            //在以为这两个日期之间的周为单位查询每周的客流数据
            List<PassengerFlowStat> stats = passengerFlowStatService.getWeeklyStat(monday, monday.plusDays(6));
            monday=monday.plusDays(7);
            while (monday.isBefore(sunday)) {
                //将每周的数据保存起来，直到所有周的数据都保存完毕
                if (stats != null) {
                    stats.addAll(passengerFlowStatService.getWeeklyStat(monday, monday.plusDays(6)));
                }
                monday=monday.plusDays(7);
            }
            stats = passengerFlowStatService.calculateRingGrowthRate(stats);
            stats = passengerFlowStatService.calculateYearOnYearGrowthRate(stats);
            System.out.println(
                    "stats: " + stats
            );
            return JsonResult.ok(stats);
        } catch (Exception e) {
            log.error("获取周客流统计数据失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    /**
     * 获取月客流统计数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 客流统计数据列表
     */
    @GetMapping("/monthly")
    public JsonResult<List<PassengerFlowStat>> getMonthlyStat(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            //传入startDate和endDate，求starData的所在月的一号，endData的所在月的月末日期
            LocalDate firstDayOfMonth = startDate.withDayOfMonth(1);
            LocalDate lastDayOfMonth = endDate.withDayOfMonth(endDate.lengthOfMonth());
            //在以为这两个日期之间的月为单位查询每周的客流数据
            List<PassengerFlowStat> stats = passengerFlowStatService.getMonthlyStat(firstDayOfMonth, firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth()));
            firstDayOfMonth=firstDayOfMonth.plusMonths(1);
            while (firstDayOfMonth.isBefore(lastDayOfMonth)) {
                //将每月的数据保存起来，直到所有周的数据都保存完毕
                if (stats != null) {
                    stats.addAll(passengerFlowStatService.getMonthlyStat(firstDayOfMonth, firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth())));
                }
                //获得下一个月的一号
                firstDayOfMonth=firstDayOfMonth.plusMonths(1);
                System.out.println("firstDayOfMonth: " + firstDayOfMonth);
            }
            stats = passengerFlowStatService.calculateRingGrowthRate(stats);
            stats = passengerFlowStatService.calculateYearOnYearGrowthRate(stats);
            stats = passengerFlowStatService.markHolidays(stats);
            return JsonResult.ok(stats);
        } catch (Exception e) {
            log.error("获取月客流统计数据失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }
}