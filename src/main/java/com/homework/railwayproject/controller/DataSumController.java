package com.homework.railwayproject.controller;

import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.pojo.dto.StatisticsResult;
import com.homework.railwayproject.pojo.entity.Line;
import com.homework.railwayproject.service.DataSumService;
import com.homework.railwayproject.web.JsonResult;
import com.homework.railwayproject.web.ServiceCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据统计控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/data-sum")
@Tag(name = "数据统计", description = "数据统计相关API")
public class DataSumController {

    @Autowired
    private DataSumService dataSumService;

    /**
     * 统计清洗表中的数据量
     *
     * @return 统计结果
     */
    @GetMapping("/clean-data-count")
    @Operation(summary = "统计清洗表数据量", description = "统计清洗表中的数据总条数")
    public JsonResult<StatisticsResult> getCleanDataCount() {
        try {
            StatisticsResult result = dataSumService.countCleanData();
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("统计清洗表数据量失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, "统计清洗表数据量失败"));
        }
    }

    /**
     * 统计线路数量
     *
     * @return 统计结果
     */
    @GetMapping("/lines")
    @Operation(summary = "统计线路数量", description = "统计系统中的线路总数量")
    public JsonResult<StatisticsResult> getLineCount() {
        try {
            StatisticsResult result = dataSumService.countLines();
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("统计线路数量失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, "统计线路数量失败"));
        }
    }

    /**
     * 统计站点数量
     *
     * @return 统计结果
     */
    @GetMapping("/station-count")
    @Operation(summary = "统计站点数量", description = "统计系统中的站点总数量")
    public JsonResult<StatisticsResult> getStationCount() {
        try {
            StatisticsResult result = dataSumService.countStations();
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("统计站点数量失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, "统计站点数量失败"));
        }
    }
}