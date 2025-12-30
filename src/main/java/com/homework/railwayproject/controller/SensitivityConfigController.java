package com.homework.railwayproject.controller;

import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.pojo.entity.SensitivityConfig;
import com.homework.railwayproject.service.SensitivityConfigService;
import com.homework.railwayproject.web.JsonResult;
import com.homework.railwayproject.web.ServiceCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 灵敏度配置控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/sensitivity-config")
@Tag(name = "灵敏度配置", description = "灵敏度配置相关API")
public class SensitivityConfigController {

    @Autowired
    private SensitivityConfigService sensitivityConfigService;

    /**
     * 根据配置类型获取灵敏度配置
     *
     * @param configType 配置类型
     * @return 灵敏度配置
     */
    @GetMapping("/get-by-type")
    @Operation(summary = "根据配置类型获取灵敏度配置", description = "根据配置类型获取灵敏度配置")
    public JsonResult<SensitivityConfig> getSensitivityByConfigType(
            @Parameter(description = "配置类型") @RequestParam String configType) {
        log.info("接收到获取配置请求 - configType: {}", configType);
        SensitivityConfig config = sensitivityConfigService.getSensitivityByConfigType(configType);
        log.info("获取到配置: {}", config);
        return JsonResult.ok(config);
    }

    /**
     * 根据配置类型更新灵敏度值
     *
     * @param configType 配置类型
     * @param sensitivityValue 灵敏度值
     * @return 操作结果
     */
    @PostMapping("/update-by-type")
    @Operation(summary = "根据配置类型更新灵敏度值", description = "根据配置类型更新灵敏度值")
    public JsonResult<Boolean> updateSensitivityByConfigType(
            @Parameter(description = "配置类型") @RequestParam String configType,
            @Parameter(description = "灵敏度值(0.0-1.0)") @RequestParam Double sensitivityValue) {
        log.info("接收到更新请求 - configType: {}, sensitivityValue: {}", configType, sensitivityValue);
        boolean result = sensitivityConfigService.updateSensitivityByConfigType(configType, sensitivityValue);
        log.info("更新结果: {}", result);
        return result ? JsonResult.ok(result) : JsonResult.fail(new ServiceException(ServiceCode.ERROR_UPDATE, "更新失败"));
    }

    /**
     * 获取高峰时段统计的灵敏度配置
     *
     * @return 灵敏度值
     */
    @GetMapping("/peak-hour")
    @Operation(summary = "获取高峰时段统计的灵敏度配置", description = "获取高峰时段统计的灵敏度配置")
    public JsonResult<Double> getPeakHourSensitivity() {
        log.info("接收到获取高峰时段灵敏度请求");
        Double sensitivity = sensitivityConfigService.getPeakHourSensitivity();
        log.info("获取到高峰时段灵敏度: {}", sensitivity);
        return JsonResult.ok(sensitivity);
    }

    /**
     * 更新高峰时段统计的灵敏度配置
     *
     * @param sensitivityValue 灵敏度值
     * @return 操作结果
     */
    @PostMapping("/peak-hour")
    @Operation(summary = "更新高峰时段统计的灵敏度配置", description = "更新高峰时段统计的灵敏度配置")
    public JsonResult<Boolean> updatePeakHourSensitivity(
            @Parameter(description = "灵敏度值(0.0-1.0)") @RequestParam Double sensitivityValue) {
        log.info("接收到高峰时段灵敏度更新请求 - sensitivityValue: {}", sensitivityValue);
        log.info("前端发送的参数类型为: Double, 值为: {}, 参数类型class: {}", sensitivityValue, sensitivityValue != null ? sensitivityValue.getClass().getSimpleName() : "null");
        boolean result = sensitivityConfigService.updatePeakHourSensitivity(sensitivityValue);
        log.info("更新结果: {}", result);
        return result ? JsonResult.ok(result) : JsonResult.fail(new ServiceException(ServiceCode.ERROR_UPDATE, "更新失败"));
    }
}