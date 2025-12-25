package com.homework.railwayproject.controller;

import com.homework.railwayproject.pojo.entity.BusyIndexStat;
import com.homework.railwayproject.service.BusyIndexStatService;
import com.homework.railwayproject.web.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
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
 * 繁忙指数统计控制器
 */
@Slf4j
@RestController
@RequestMapping("/busy-index-stat")
@Tag(name = "站点繁忙指数统计", description = "站点繁忙指数统计相关API")
public class BusyIndexStatController {

    @Autowired
    private BusyIndexStatService busyIndexStatService;

    /**
     * 获取站点繁忙指数前20名
     *
     * @return 站点繁忙指数统计列表
     */
    @GetMapping("/top20")
    @Operation(summary = "获取站点繁忙指数前20名", description = "获取站点繁忙指数前20名")
    public JsonResult<List<BusyIndexStat>> getTop20BusyIndexStations() {
        List<BusyIndexStat> list = busyIndexStatService.getTop20BusyIndexStations();
        return JsonResult.ok(list);
    }
//传入站点id和 时间startTime和endTime
    @GetMapping("/by-id-and-time")
    @Operation(summary = "获取站点繁忙指数", description = "获取站点繁忙指数")
    public JsonResult<BusyIndexStat> getBusyIndexStatByIdAndTime(@RequestParam Integer siteId,@RequestParam
    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime, @RequestParam
    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime) {
        BusyIndexStat busyIndexStat = busyIndexStatService.getBusyIndexStatByIdAndTime(siteId, startTime, endTime);
        return JsonResult.ok(busyIndexStat);
    }
}