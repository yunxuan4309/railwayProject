package com.homework.railwayproject.controller;

import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.pojo.dto.SectionLoadRateQueryDTO;
import com.homework.railwayproject.pojo.vo.LoadRateVO;
import com.homework.railwayproject.pojo.vo.OverloadAlertVO;
import com.homework.railwayproject.pojo.vo.TrainAdditionSuggestionVO;
import com.homework.railwayproject.service.LineOptimizationService;
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
@RequestMapping("/api/line-optimization")
@Tag(name = "线路优化接口", description = "线路优化相关的接口，包括满载率、过载告警和加车建议")
public class LineOptimizationController {

    @Autowired
    private LineOptimizationService lineOptimizationService;

    /**
     * 1. 满载率接口
     * 计算每个区间（相邻两站之间）每小时的满载率
     * 结果存缓存5分钟
     */
    @GetMapping("/load-rate/hourly")
    @Operation(summary = "获取区间每小时满载率", description = "计算每个区间每小时的满载率，结果缓存5分钟")
    public JsonResult<List<LoadRateVO>> getSectionLoadRateHourly(
            @Parameter(description = "线路编码") @RequestParam(required = false) String lineCode,
            @Parameter(description = "统计日期，格式：yyyy-MM-dd")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate flowDate) {
        try {
            SectionLoadRateQueryDTO query = new SectionLoadRateQueryDTO();
            query.setLineCode(lineCode);
            query.setFlowDate(flowDate);

            List<LoadRateVO> result = lineOptimizationService.calculateSectionLoadRate(query);
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("获取区间满载率失败", e);
            // 使用 ServiceException 包装错误
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    /**
     * 2. 过载告警接口
     * 如果某个区间连续7天上座率超90%，就在Dashboard上显示红色告警卡片
     */
    @GetMapping("/overload-alerts")
    @Operation(summary = "获取过载告警", description = "获取连续7天上座率超90%的区间告警")
    public JsonResult<List<OverloadAlertVO>> getOverloadAlerts() {
        try {
            List<OverloadAlertVO> alerts = lineOptimizationService.getOverloadAlerts();
            return JsonResult.ok(alerts);
        } catch (Exception e) {
            log.error("获取过载告警失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    /**
     * 3. 加车建议接口
     * 针对过载区间，生成一个加开车次的建议表
     * （车次、时刻、编组），只供人工参考，不发真实调度指令
     */
    @GetMapping("/addition-suggestions")
    @Operation(summary = "获取加车建议", description = "针对过载区间生成加开车次的建议")
    public JsonResult<List<TrainAdditionSuggestionVO>> getAdditionSuggestions() {
        try {
            List<TrainAdditionSuggestionVO> suggestions = lineOptimizationService.generateAdditionSuggestions();
            return JsonResult.ok(suggestions);
        } catch (Exception e) {
            log.error("获取加车建议失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    /**
     * 手动触发区间客流统计任务（用于测试或补数据）
     */
    @PostMapping("/trigger-statistics")
    @Operation(summary = "触发区间客流统计", description = "手动触发区间客流统计任务")
    public JsonResult<String> triggerStatistics(
            @Parameter(description = "统计日期，格式：yyyy-MM-dd")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate flowDate) {
        try {
            lineOptimizationService.calculateAndSaveSectionStatistics(flowDate);
            return JsonResult.ok("区间客流统计任务已启动");
        } catch (Exception e) {
            log.error("触发统计任务失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }
}