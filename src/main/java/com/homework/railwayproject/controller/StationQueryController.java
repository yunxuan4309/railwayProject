package com.homework.railwayproject.controller;

import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.pojo.entity.Station;
import com.homework.railwayproject.service.StationService;
import com.homework.railwayproject.web.JsonResult;
import com.homework.railwayproject.web.ServiceCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/station")
@Tag(name = "站点查询接口", description = "用于查询站点相关信息的接口")
public class StationQueryController {

    @Autowired
    private StationService stationService;

    /**
     * 获取所有站点信息
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有站点信息", description = "返回所有站点的基本信息，用于前端下拉选择")
    public JsonResult<List<Station>> getAllStations() {
        try {
            // 使用MyBatis-Plus的list方法
            List<Station> stations = stationService.list();
            // 移除敏感信息，只返回需要的字段
            for (Station station : stations) {
                station.setCreateTime(null);
                station.setUpdateTime(null);
                station.setCreateBy(null);
                station.setUpdateBy(null);
            }
            return JsonResult.ok(stations);
        } catch (Exception e) {
            log.error("获取站点信息失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, "获取站点信息失败"));
        }
    }

    /**
     * 根据名称查询站点
     */
    @GetMapping("/search")
    @Operation(summary = "根据名称查询站点", description = "根据站点名称模糊查询站点信息")
    public JsonResult<List<Station>> searchStations(@Parameter(description = "站点名称") @RequestParam String stationName) {
        try {
            List<Station> stations = stationService.searchStationsByName(stationName);
            // 移除敏感信息，只返回需要的字段
            for (Station station : stations) {
                station.setCreateTime(null);
                station.setUpdateTime(null);
                station.setCreateBy(null);
                station.setUpdateBy(null);
            }
            return JsonResult.ok(stations);
        } catch (Exception e) {
            log.error("查询站点信息失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, "查询站点信息失败"));
        }
    }
}