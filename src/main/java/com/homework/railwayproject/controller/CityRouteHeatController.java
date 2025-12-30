package com.homework.railwayproject.controller;

import com.homework.railwayproject.pojo.entity.CityRouteHeat;
import com.homework.railwayproject.service.CityRouteHeatService;
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
 * 城市路线热度控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/city-route-heat")
@Tag(name = "城市路线热度", description = "城市间客流热度分析API")
public class CityRouteHeatController {
    
    @Autowired
    private CityRouteHeatService cityRouteHeatService;
    
    /**
     * 获取指定日期的城市路线热度排行
     *
     * @param date 指定日期
     * @return 城市路线热度列表
     */
    @GetMapping("/by-date")
    @Operation(summary = "获取指定日期的城市路线热度排行", description = "获取指定日期的城市路线热度排行，热度值=日客流/线路总客流，避免大城市霸榜")
    public JsonResult<List<CityRouteHeat>> getCityRouteHeatByDate(
            @Parameter(description = "日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<CityRouteHeat> list = cityRouteHeatService.getCityRouteHeatByDate(date);
        return JsonResult.ok(list);
    }
}