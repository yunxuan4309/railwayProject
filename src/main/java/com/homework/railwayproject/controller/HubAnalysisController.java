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
