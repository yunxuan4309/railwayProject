package com.homework.railwayproject.controller;

import com.homework.railwayproject.pojo.entity.StationRole;
import com.homework.railwayproject.service.StationRoleClassifier;
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

/**
 * 站点角色控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/station-role")
@Tag(name = "站点角色", description = "站点角色分类相关API")
public class StationRoleController {

    @Autowired
    private StationRoleClassifier stationRoleClassifier;

    /**
     * 获取指定站点在指定日期的角色分类
     *
     * 站点角色判断规则基于客流数据中的比例计算得出：
     * - 始发站: 发送比例 ≥ 60% (发送人数 / 总客流量)
     * - 终到站: 到达比例 ≥ 60% (到达人数 / 总客流量)
     * - 中转站: 中转比例 ≥ 40% (中转人数 / 总客流量)
     * - 通过站: 不符合以上条件的普通站点
     *
     * @param stationId    站点ID
     * @param analysisDate 分析日期
     * @return 站点角色
     */
    @GetMapping("/classify")
    @Operation(summary = "获取指定站点的角色分类", description = "根据指定日期的客流数据分析站点角色")
    public JsonResult<StationRole> classifyStation(
            @Parameter(description = "站点ID") @RequestParam Integer stationId,
            @Parameter(description = "分析日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate analysisDate) {
        StationRole role = stationRoleClassifier.classifyStation(stationId, analysisDate);
        return JsonResult.ok(role);
    }
}