package com.homework.railwayproject.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.pojo.dto.LineSectionLoadRateQueryDTO;
import com.homework.railwayproject.pojo.vo.LineSectionLoadRateVO;
import com.homework.railwayproject.service.LineSectionLoadRateMapService;
import com.homework.railwayproject.web.JsonResult;
import com.homework.railwayproject.web.ServiceCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/line-section-map")
@Tag(name = "线路断面满载率地图接口", description = "用于展示线路断面满载率分布的可视化地图接口")
public class LineSectionLoadRateMapController {

    @Autowired
    private LineSectionLoadRateMapService lineSectionLoadRateMapService;

    /**
     * 获取线路断面满载率地图数据
     * 用于前端展示各线路断面的满载率分布情况
     */
    @GetMapping("/load-rate-map")
    @Operation(summary = "获取线路断面满载率地图数据", description = "返回各线路断面的满载率分布，用于地图可视化展示")
    public JsonResult<IPage<LineSectionLoadRateVO>> getLineSectionLoadRateMap(
            @Parameter(description = "线路编码") @RequestParam(required = false) String lineCode,
            @Parameter(description = "统计日期，格式：yyyy-MM-dd")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate flowDate,
            @Parameter(description = "开始日期，格式：yyyy-MM-dd")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期，格式：yyyy-MM-dd")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "开始小时") @RequestParam(required = false) Integer startHour,
            @Parameter(description = "结束小时") @RequestParam(required = false) Integer endHour,
            @Parameter(description = "起始站ID") @RequestParam(required = false) Integer startStationId,
            @Parameter(description = "终点站ID") @RequestParam(required = false) Integer endStationId,
            @Parameter(description = "起始站名称") @RequestParam(required = false) String startStationName,
            @Parameter(description = "终点站名称") @RequestParam(required = false) String endStationName,
            @Parameter(description = "页码，从1开始") @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(required = false, defaultValue = "10") Integer size) {
        try {
            LineSectionLoadRateQueryDTO query = new LineSectionLoadRateQueryDTO();
            query.setLineCode(lineCode);
            query.setFlowDate(flowDate);
            query.setStartDate(startDate);
            query.setEndDate(endDate);
            query.setStartHour(startHour);
            query.setEndHour(endHour);
            query.setStartStationId(startStationId);
            query.setEndStationId(endStationId);
            query.setStartStationName(startStationName);
            query.setEndStationName(endStationName);
            query.setPage(page);
            query.setSize(size);

            IPage<LineSectionLoadRateVO> result = lineSectionLoadRateMapService.getLineSectionLoadRateMapWithPaging(query);
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("获取线路断面满载率地图数据失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    /**
     * 获取所有线路的基本信息（用于地图线路选择）
     */
    @GetMapping("/lines")
    @Operation(summary = "获取所有线路信息", description = "返回所有线路的基本信息，用于地图界面的线路选择")
    public JsonResult<List<LineSectionLoadRateVO>> getAllLines() {
        try {
            List<LineSectionLoadRateVO> result = lineSectionLoadRateMapService.getAllLines();
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("获取线路信息失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }
}