package com.homework.railwayproject.controller;

import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.pojo.dto.HubQueryDTO;
import com.homework.railwayproject.pojo.dto.HubResultDTO;
import com.homework.railwayproject.service.HubAnalysisService;
import com.homework.railwayproject.web.JsonResult;
import com.homework.railwayproject.web.ServiceCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 枢纽识别控制器
 * 提供基于度中心性和介数中心性的枢纽识别API
 */
@Slf4j
@RestController
@RequestMapping("/api/hub")
@Tag(name = "枢纽识别", description = "枢纽识别分析API")
public class HubAnalysisController {

    @Autowired
    private HubAnalysisService hubAnalysisService;

    /**
     * 获取TOP N个枢纽（POST请求）
     * 
     * 枢纽识别算法基于复杂网络分析理论，采用度中心性和介数中心性两个核心指标评估站点重要性：
     * 1. 度中心性（Degree Centrality）：衡量站点直接连接数，反映站点的网络连接度
     *    - 计算方式：站点在客运网络中的邻接边数量（上客量+下客量）
     *    - 意义：度中心性高的站点通常是重要的客运集散地
     * 2. 介数中心性（Betweenness Centrality）：衡量站点在网络最短路径中的重要性，反映站点的中介作用
     *    - 计算方式：经过该站点的最短路径数量占总最短路径数量的比例
     *    - 意义：介数中心性高的站点通常是重要的换乘节点
     * 3. 枢纽级别评定：根据综合评分划分枢纽等级
     *    - 评分公式：ln(度中心性+1)*10 + 介数中心性*1000
     *    - 特级枢纽：评分≥200
     *    - 一级枢纽：评分≥150
     *    - 二级枢纽：评分≥100
     *    - 三级枢纽：评分≥50
     *    - 四级枢纽：评分≥20
     *    - 五级枢纽：评分<20
     * 4. 数据筛选机制：
     *    - trainType参数控制数据筛选范围，不同网别下同一站点的度中心性和枢纽级别可能不同
     *    - 高铁：仅统计车次以"G"开头的列车数据
     *    - 城际：仅统计车次以"C"开头的列车数据
     *    - 普速：统计车次不以"G"或"C"开头的列车数据
     *    - 不同网别下同一站点的连接度和重要性会有所差异
     * 
     * @param queryDTO 查询参数
     *                 - trainType: 网别（高铁/城际/普速）
     *                 - topN: 返回TOP N个枢纽（默认10）
     * @return 枢纽列表，包含站点信息、度中心性、介数中心性、枢纽级别
     */
    @PostMapping("/top")
    @Operation(summary = "获取TOP枢纽", description = "基于度中心性和介数中心性计算TOP10枢纽，支持按网别过滤")
    public JsonResult<List<HubResultDTO>> getTopHubs(@RequestBody HubQueryDTO queryDTO) {
        try {
            List<HubResultDTO> result = hubAnalysisService.getTopHubs(queryDTO);
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("获取TOP枢纽失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    /**
     * 获取TOP N个枢纽（GET请求，通过URL参数传递）
     * 
     * 枢纽识别算法基于复杂网络分析理论，采用度中心性和介数中心性两个核心指标评估站点重要性：
     * 1. 度中心性（Degree Centrality）：衡量站点直接连接数，反映站点的网络连接度
     *    - 计算方式：站点在客运网络中的邻接边数量（上客量+下客量）
     *    - 意义：度中心性高的站点通常是重要的客运集散地
     * 2. 介数中心性（Betweenness Centrality）：衡量站点在网络最短路径中的重要性，反映站点的中介作用
     *    - 计算方式：经过该站点的最短路径数量占总最短路径数量的比例
     *    - 意义：介数中心性高的站点通常是重要的换乘节点
     * 3. 枢纽级别评定：根据综合评分划分枢纽等级
     *    - 评分公式：ln(度中心性+1)*10 + 介数中心性*1000
     *    - 特级枢纽：评分≥200
     *    - 一级枢纽：评分≥150
     *    - 二级枢纽：评分≥100
     *    - 三级枢纽：评分≥50
     *    - 四级枢纽：评分≥20
     *    - 五级枢纽：评分<20
     * 4. 数据筛选机制：
     *    - trainType参数控制数据筛选范围，不同网别下同一站点的度中心性和枢纽级别可能不同
     *    - 高铁：仅统计车次以"G"开头的列车数据
     *    - 城际：仅统计车次以"C"开头的列车数据
     *    - 普速：统计车次不以"G"或"C"开头的列车数据
     *    - 不同网别下同一站点的连接度和重要性会有所差异
     * 
     * @param trainType 网别（高铁/城际/普速），可选
     * @param topN 返回TOP N个枢纽（默认10），可选
     * @return 枢纽列表，包含站点信息、度中心性、介数中心性、枢纽级别
     */
    @GetMapping("/top")
    @Operation(summary = "获取TOP枢纽（GET）", description = "基于度中心性和介数中心性计算TOP10枢纽，支持按网别过滤，通过URL参数传递")
    public JsonResult<List<HubResultDTO>> getTopHubsByGet(
            @RequestParam(required = false) String trainType,
            @RequestParam(required = false, defaultValue = "10") Integer topN) {
        try {
            HubQueryDTO queryDTO = new HubQueryDTO();
            queryDTO.setTrainType(trainType);
            queryDTO.setTopN(topN);
            List<HubResultDTO> result = hubAnalysisService.getTopHubs(queryDTO);
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("获取TOP枢纽失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }
}
