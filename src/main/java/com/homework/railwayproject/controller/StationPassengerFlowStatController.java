package com.homework.railwayproject.controller;

import com.homework.railwayproject.pojo.entity.StationPassengerFlowStat;
import com.homework.railwayproject.service.StationPassengerFlowStatService;
import com.homework.railwayproject.web.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Operation(summary = "获取站点客流统计前20名", description = "获取站点客流统计前20名")
    public JsonResult<List<StationPassengerFlowStat>> getTop20Stations() {
        List<StationPassengerFlowStat> list = stationPassengerFlowStatService.getTop20Stations();
        return JsonResult.ok(list);
    }
    //获取传入的站点的客流量
    @GetMapping("/getStationPassengerFlow")
    @Operation(summary = "获取传入的站点的客流量", description = "获取传入的站点的客流量")
    public JsonResult<StationPassengerFlowStat> getStationPassengerFlow(Integer siteId) {
        StationPassengerFlowStat stationPassengerFlowStat = stationPassengerFlowStatService.getStationPassengerFlow(siteId);
        return JsonResult.ok(stationPassengerFlowStat);
    }
}