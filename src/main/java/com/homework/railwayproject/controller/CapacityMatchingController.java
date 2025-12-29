package com.homework.railwayproject.controller;

import com.homework.railwayproject.pojo.entity.CapacityMatchingStat;
import com.homework.railwayproject.service.CapacityMatchingService;
import com.homework.railwayproject.web.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 容量匹配度控制器
 * 用于处理高峰客流与站台容量匹配度相关的API请求
 */
@Tag(name = "容量匹配度统计", description = "评估高峰客流与站台容量匹配情况的接口")
@RestController
@RequestMapping("/api/capacity-matching")
public class CapacityMatchingController {
    
    @Autowired
    private CapacityMatchingService capacityMatchingService;
    
    /**
     * 计算单个站点的容量匹配度（根据站点ID自动获取站台容量）
     * 
     * @param siteId 站点ID
     * @param peakPassengerFlow 高峰期客流量，高峰接口所计算出来的结果
     * @return 容量匹配度统计信息
     */
    @Operation(summary = "根据站点ID计算容量匹配度", description = "根据站点ID自动获取站台容量，计算容量匹配度")
    @GetMapping("/calculate-by-site")
    public JsonResult<CapacityMatchingStat> calculateCapacityMatchingBySiteId(
            @RequestParam Integer siteId,
            @RequestParam Integer peakPassengerFlow) {

        CapacityMatchingStat result = capacityMatchingService.calculateCapacityMatchingBySiteId(
                siteId, peakPassengerFlow);
        return JsonResult.ok(result);
    }
}