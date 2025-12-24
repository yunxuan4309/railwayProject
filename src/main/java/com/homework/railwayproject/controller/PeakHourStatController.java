package com.homework.railwayproject.controller;

import com.homework.railwayproject.pojo.entity.PeakHourStat;
import com.homework.railwayproject.service.PeakHourStatService;
import com.homework.railwayproject.web.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * 高峰时段统计控制器
 */
@Slf4j
@RestController
@RequestMapping("/peak-hour-stat")
@Tag(name = "高峰时段统计", description = "高峰时段统计相关API")
public class PeakHourStatController {
    
    @Autowired
    private PeakHourStatService peakHourStatService;
    
    /**
     * 获取指定日期的高峰时段统计信息
     *
     * @param date 指定日期
     * @return 高峰时段统计列表
     */
    @GetMapping("/peak-hours")
    @Operation(summary = "获取指定日期的高峰时段统计信息", description = "获取指定日期的高峰时段统计信息")
    public JsonResult<List<PeakHourStat>> getPeakHours(
            @Parameter(description = "日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<PeakHourStat> list = peakHourStatService.getPeakHours(date);
        return JsonResult.ok(list);
    }
    
    /**
     * 获取最拥挤的三个连续时段
     *
     * @param date 指定日期
     * @return 最拥挤的三个连续时段列表
     */
    @GetMapping("/top3-consecutive-peaks")
    @Operation(summary = "获取最拥挤的三个连续时段", description = "获取最拥挤的三个连续时段")
    public JsonResult<List<PeakHourStat>> getTop3ConsecutivePeakHours(
            @Parameter(description = "日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @Parameter(description = "灵敏度调节参数(0.0-1.0)，默认0.15") @RequestParam(defaultValue = "0.15") Double sensitivity) {
        List<PeakHourStat> list = peakHourStatService.getTop3ConsecutivePeakHours(date, sensitivity);
        return JsonResult.ok(list);
    }
}