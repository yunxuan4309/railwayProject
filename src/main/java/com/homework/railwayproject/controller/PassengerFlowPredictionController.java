package com.homework.railwayproject.controller;

import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.pojo.dto.PassengerFlowPredictionDTO;
import com.homework.railwayproject.pojo.entity.PassengerFlowPrediction;
import com.homework.railwayproject.service.PassengerFlowPredictionService;
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

/**
 * 客流预测控制器
 * 提供短期客流预测功能
 */
@Slf4j
@RestController
@RequestMapping("/api/passenger-flow-prediction")
@Tag(name = "客流预测", description = "短期客流预测相关API")
public class PassengerFlowPredictionController {

    @Autowired
    private PassengerFlowPredictionService passengerFlowPredictionService;

    /**
     * 使用移动平均算法进行短期客流预测
     * 
     * @param siteId 站点ID
     * @param predictionDate 预测日期
     * @param daysForPrediction 用于预测的历史天数（默认为7天）
     * @return 客流预测结果列表
     */
    @GetMapping("/predict-by-moving-average")
    @Operation(summary = "使用移动平均算法进行短期客流预测", description = "基于历史N天数据使用移动平均算法预测指定站点的客流")
    public JsonResult<List<PassengerFlowPredictionDTO>> predictByMovingAverage(
            @Parameter(description = "站点ID") @RequestParam Integer siteId,
            @Parameter(description = "预测日期，格式：yyyy-MM-dd") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate predictionDate,
            @Parameter(description = "用于预测的历史天数，默认为7天") @RequestParam(defaultValue = "7") Integer daysForPrediction) {
        
        try {
            List<PassengerFlowPredictionDTO> predictions = 
                passengerFlowPredictionService.predictPassengerFlowByMovingAverage(siteId, predictionDate, daysForPrediction);
            
            if (predictions.isEmpty()) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, "客流信息不足，无法进行移动平均预测"));
            }
            
            // 保存预测结果到数据库
            passengerFlowPredictionService.savePredictionDTOs(predictions, "移动平均算法");
            
            return JsonResult.ok(predictions);
        } catch (Exception e) {
            log.error("移动平均算法客流预测失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, "客流预测失败: " + e.getMessage()));
        }
    }

    /**
     * 使用周期性分析算法进行短期客流预测
     * 
     * @param siteId 站点ID
     * @param predictionDate 预测日期
     * @return 客流预测结果列表
     */
    @GetMapping("/predict-by-periodicity")
    @Operation(summary = "使用周期性分析算法进行短期客流预测", description = "基于历史同期数据使用周期性分析算法预测指定站点的客流")
    public JsonResult<List<PassengerFlowPredictionDTO>> predictByPeriodicity(
            @Parameter(description = "站点ID") @RequestParam Integer siteId,
            @Parameter(description = "预测日期，格式：yyyy-MM-dd") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate predictionDate) {
        
        try {
            List<PassengerFlowPredictionDTO> predictions = 
                passengerFlowPredictionService.predictPassengerFlowByPeriodicity(siteId, predictionDate);
            
            if (predictions.isEmpty()) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, "客流信息不足，无法进行周期性预测"));
            }
            
            // 保存预测结果到数据库
            passengerFlowPredictionService.savePredictionDTOs(predictions, "周期性分析算法");
            
            return JsonResult.ok(predictions);
        } catch (Exception e) {
            log.error("周期性分析算法客流预测失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, "客流预测失败: " + e.getMessage()));
        }
    }

    /**
     * 获取指定站点和日期范围内的客流预测结果
     * 
     * @param siteId 站点ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 客流预测结果列表
     */
    @GetMapping("/predictions-by-date-range")
    @Operation(summary = "获取指定站点和日期范围内的客流预测结果", description = "查询指定站点在指定日期范围内的已预测客流数据")
    public JsonResult<List<PassengerFlowPrediction>> getPredictionsByDateRange(
            @Parameter(description = "站点ID") @RequestParam Integer siteId,
            @Parameter(description = "开始日期，格式：yyyy-MM-dd") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期，格式：yyyy-MM-dd") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        try {
            List<PassengerFlowPrediction> predictions = 
                passengerFlowPredictionService.getPredictionsBySiteAndDateRange(siteId, startDate, endDate);
            
            return JsonResult.ok(predictions);
        } catch (Exception e) {
            log.error("查询客流预测结果失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, "查询客流预测结果失败: " + e.getMessage()));
        }
    }

    /**
     * 获取指定日期的客流预测结果
     * 
     * @param predictionDate 预测日期
     * @return 客流预测结果列表
     */
    @GetMapping("/predictions-by-date")
    @Operation(summary = "获取指定日期的客流预测结果", description = "查询指定日期的所有站点客流预测数据")
    public JsonResult<List<PassengerFlowPrediction>> getPredictionsByDate(
            @Parameter(description = "预测日期，格式：yyyy-MM-dd") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate predictionDate) {
        
        try {
            // 这里暂时返回空列表，因为我们的预测是实时计算的，不存储在数据库中
            // 在实际应用中，预测结果会被保存到数据库中
            return JsonResult.ok(null);
        } catch (Exception e) {
            log.error("查询指定日期的客流预测结果失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, "查询指定日期的客流预测结果失败: " + e.getMessage()));
        }
    }
}