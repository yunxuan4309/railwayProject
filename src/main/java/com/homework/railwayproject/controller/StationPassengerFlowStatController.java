package com.homework.railwayproject.controller;

import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.pojo.entity.StationPassengerFlowStat;
import com.homework.railwayproject.service.StationPassengerFlowStatService;
import com.homework.railwayproject.web.JsonResult;
import com.homework.railwayproject.web.ServiceCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 站点客流统计控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/station-passenger-flow-stat")
@Tag(name = "站点客流统计", description = "站点客流统计相关API")
public class StationPassengerFlowStatController {
    
    @Autowired
    private StationPassengerFlowStatService stationPassengerFlowStatService;
    
    /**
     * 获取站点客流统计前20名
     *
     * @return 站点客流统计列表
     */
    @GetMapping("/top20")
    @Operation(summary = "获取站点客流统计前20名", description = "获取站点客流统计前20名（默认为最近一天数据）")
    public JsonResult<List<StationPassengerFlowStat>> getTop20Stations() {
        List<StationPassengerFlowStat> list = stationPassengerFlowStatService.getTop20Stations();
        return JsonResult.ok(list);
    }
    
    /**
     * 根据指定日期范围获取站点客流统计前20名
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 站点客流统计列表
     */
    @GetMapping("/top20-by-date")
    @Operation(summary = "根据指定日期范围获取站点客流统计前20名", description = "根据指定日期范围获取站点客流统计前20名")
    public JsonResult<List<StationPassengerFlowStat>> getTop20StationsByDate(
            @RequestParam @Parameter(description = "开始日期，格式：yyyy-MM-dd") LocalDate startDate,
            @RequestParam @Parameter(description = "结束日期，格式：yyyy-MM-dd") LocalDate endDate) {
        try {
            List<StationPassengerFlowStat> list = stationPassengerFlowStatService.getTop20StationsByDate(startDate, endDate);
            return JsonResult.ok(list);
        } catch (IllegalArgumentException e) {
            ServiceException se = new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage());
            return JsonResult.fail(se);
        }
    }
    
    //获取传入的站点的客流量
    @GetMapping("/getStationPassengerFlow")
    @Operation(summary = "获取传入的站点的客流量", description = "获取传入的站点的客流量（默认为全部历史数据）")
    public JsonResult<StationPassengerFlowStat> getStationPassengerFlow(@RequestParam @Parameter(description = "站点ID") Integer siteId) {
        try {
            StationPassengerFlowStat stationPassengerFlowStat = stationPassengerFlowStatService.getStationPassengerFlow(siteId);
            return JsonResult.ok(stationPassengerFlowStat);
        } catch (IllegalArgumentException e) {
            ServiceException se = new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage());
            return JsonResult.fail(se);
        }
    }
    
    /**
     * 获取指定站点在指定日期范围内的客流量
     *
     * @param siteId 站点ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 站点客流统计数据
     */
    @GetMapping("/getStationPassengerFlowByDate")
    @Operation(summary = "获取指定站点在指定日期范围内的客流量", description = "获取指定站点在指定日期范围内的客流量")
    public JsonResult<StationPassengerFlowStat> getStationPassengerFlowByDate(
            @RequestParam @Parameter(description = "站点ID") Integer siteId,
            @RequestParam @Parameter(description = "开始日期，格式：yyyy-MM-dd") LocalDate startDate,
            @RequestParam @Parameter(description = "结束日期，格式：yyyy-MM-dd") LocalDate endDate) {
        try {
            StationPassengerFlowStat stationPassengerFlowStat = stationPassengerFlowStatService.getStationPassengerFlowByDate(siteId, startDate, endDate);
            return JsonResult.ok(stationPassengerFlowStat);
        } catch (IllegalArgumentException e) {
            ServiceException se = new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage());
            return JsonResult.fail(se);
        }
    }
}