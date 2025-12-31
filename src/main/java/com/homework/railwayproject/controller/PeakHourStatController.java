package com.homework.railwayproject.controller;

import com.homework.railwayproject.pojo.dto.StationPeakHourStatDTO;
import com.homework.railwayproject.pojo.entity.PeakHourStat;
import com.homework.railwayproject.service.PeakHourStatService;
import com.homework.railwayproject.service.SensitivityConfigService;
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
@RequestMapping("/api/peak-hour-stat")
@Tag(name = "高峰时段统计", description = "高峰时段统计相关API")
public class PeakHourStatController {
    
    @Autowired
    private PeakHourStatService peakHourStatService;
    
    @Autowired
    private SensitivityConfigService sensitivityConfigService;
    
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
            @Parameter(description = "日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<PeakHourStat> list = peakHourStatService.getTop3ConsecutivePeakHours(date);
        return JsonResult.ok(list);
    }
    
    /**
     * 根据站点ID和日期获取该站点客流量最高的时段
     *
     * @param stationId 站点ID
     * @param date 指定日期
     * @return 站点最高客流时段统计
     */
    @GetMapping("/top-peak-hour-by-station")
    @Operation(summary = "获取站点指定日期客流量最高的时段", description = "根据站点ID和日期获取该站点在指定日期客流量最高的时段")
    public JsonResult<StationPeakHourStatDTO> getTopPeakHourByStationId(
            @Parameter(description = "站点ID") @RequestParam Integer stationId,
            @Parameter(description = "日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        StationPeakHourStatDTO result = peakHourStatService.getTopPeakHourByStationId(stationId, date);
        return JsonResult.ok(result);
    }

    /**
     * 根据站点ID和日期获取该站点所有时段的客流量
     *
     * @param stationId 站点ID
     * @param date 指定日期
     * @return 站点所有时段客流统计列表
     */
    @GetMapping("/hourly-stat-by-station")
    @Operation(summary = "获取站点指定日期所有时段的客流量", description = "根据站点ID和日期获取该站点在指定日期所有时段的客流量")
    public JsonResult<List<PeakHourStat>> getHourlyStatByStationIdAndDate(
            @Parameter(description = "站点ID") @RequestParam Integer stationId,
            @Parameter(description = "日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<PeakHourStat> list = peakHourStatService.getHourlyStatByStationIdAndDate(stationId, date);
        return JsonResult.ok(list);
    }
}