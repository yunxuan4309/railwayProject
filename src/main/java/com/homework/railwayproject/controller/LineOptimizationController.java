package com.homework.railwayproject.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.pojo.dto.SectionLoadRateQueryDTO;
import com.homework.railwayproject.pojo.dto.TrainAdditionSuggestionQueryDTO;
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
     * 结果存缓存2小时
     */
    @GetMapping("/load-rate/hourly")
    @Operation(summary = "获取区间每小时满载率", description = "计算每个区间每小时的满载率，结果缓存2小时")
    public JsonResult<IPage<LoadRateVO>> getSectionLoadRateHourly(
            @Parameter(description = "线路编码") @RequestParam(required = false) String lineCode,
            @Parameter(description = "统计日期，格式：yyyy-MM-dd")
            @RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate flowDate,
            @Parameter(description = "开始小时") @RequestParam(required = false) Integer startHour,
            @Parameter(description = "结束小时") @RequestParam(required = false) Integer endHour,
            @Parameter(description = "起始站ID") @RequestParam(required = false) Integer startStationId,
            @Parameter(description = "终点站ID") @RequestParam(required = false) Integer endStationId,
            @Parameter(description = "起始站名称") @RequestParam(required = false) String startStationName,
            @Parameter(description = "终点站名称") @RequestParam(required = false) String endStationName,
            @Parameter(description = "页码，从1开始") @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(required = false, defaultValue = "10") Integer size) {
        try {
            SectionLoadRateQueryDTO query = new SectionLoadRateQueryDTO();
            query.setLineCode(lineCode);
            query.setFlowDate(flowDate);
            query.setStartHour(startHour);
            query.setEndHour(endHour);
            query.setStartStationId(startStationId);
            query.setEndStationId(endStationId);
            query.setStartStationName(startStationName);
            query.setEndStationName(endStationName);
            query.setCurrent(page);
            query.setSize(size);

            IPage<LoadRateVO> result = lineOptimizationService.getSectionLoadRateWithPaging(query);
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
     * 4. 根据线路编码获取加车建议（分页）
     */
    @GetMapping("/addition-suggestions-by-line")
    @Operation(summary = "根据线路编码获取加车建议（分页）", description = "根据线路编码获取加车建议，支持分页")
    public JsonResult<IPage<TrainAdditionSuggestionVO>> getAdditionSuggestionsByLineCode(
            @Parameter(description = "线路编码") @RequestParam(required = false) String lineCode,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        try {
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<TrainAdditionSuggestionVO> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
            IPage<TrainAdditionSuggestionVO> result = lineOptimizationService.getAdditionSuggestionsByLineCode(lineCode, page);
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("获取线路加车建议失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }
    
    /**
     * 5. 添加人工增开建议
     */
    @PostMapping("/addition-suggestions")
    @Operation(summary = "添加人工增开建议", description = "添加人工增开建议")
    public JsonResult<TrainAdditionSuggestionVO> addManualAdditionSuggestion(@RequestBody TrainAdditionSuggestionVO suggestion) {
        try {
            TrainAdditionSuggestionVO result = lineOptimizationService.addManualAdditionSuggestion(suggestion);
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("添加人工增开建议失败", e);
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
            @RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate flowDate) {
        try {
            lineOptimizationService.calculateAndSaveSectionStatistics(flowDate);
            return JsonResult.ok("区间客流统计任务已启动");
        } catch (Exception e) {
            log.error("触发统计任务失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }
    
    /**
     * 6. 分页查询增车建议（组合条件查询）
     */
    @GetMapping("/addition-suggestions/paging")
    @Operation(summary = "分页查询增车建议", description = "根据线路、区间、状态等条件分页查询增车建议")
    public JsonResult<IPage<TrainAdditionSuggestionVO>> queryAdditionSuggestionsWithPaging(
            @Parameter(description = "线路编码") @RequestParam(required = false) String lineCode,
            @Parameter(description = "区间") @RequestParam(required = false) String section,
            @Parameter(description = "建议状态") @RequestParam(required = false) String status,
            @Parameter(description = "创建者类型") @RequestParam(required = false) String createdBy,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        try {
            TrainAdditionSuggestionQueryDTO query = new TrainAdditionSuggestionQueryDTO();
            query.setLineCode(lineCode);
            query.setSection(section);
            query.setStatus(status);
            query.setCreatedBy(createdBy);
            query.setCurrent(current);
            query.setSize(size);
            
            IPage<TrainAdditionSuggestionVO> result = lineOptimizationService.querySuggestionsWithPaging(query);
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("分页查询增车建议失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }
    
    /**
     * 7. 更新增车建议状态（审核功能）
     */
    @PutMapping("/addition-suggestions/{id}/status")
    @Operation(summary = "更新增车建议状态", description = "审核增车建议，更新其状态（PENDING/APPROVED/REJECTED）")
    public JsonResult<TrainAdditionSuggestionVO> updateSuggestionStatus(
            @Parameter(description = "建议ID") @PathVariable Long id,
            @Parameter(description = "新状态") @RequestParam String status) {
        try {
            TrainAdditionSuggestionVO updatedSuggestion = lineOptimizationService.updateSuggestionStatus(id, status);
            if (updatedSuggestion != null) {
                return JsonResult.ok(updatedSuggestion);
            } else {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_NOT_FOUND, "找不到指定的增车建议"));
            }
        } catch (Exception e) {
            log.error("更新增车建议状态失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }
}