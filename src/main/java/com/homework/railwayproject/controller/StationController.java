package com.homework.railwayproject.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.pojo.dto.StationLevelStatDTO;
import com.homework.railwayproject.pojo.dto.StationLevelValidateDTO;
import com.homework.railwayproject.pojo.dto.StationLevelValidateResultDTO;
import com.homework.railwayproject.pojo.entity.Station;
import com.homework.railwayproject.service.StationService;
import com.homework.railwayproject.web.JsonResult;
import com.homework.railwayproject.web.ServiceCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/station")
@Tag(name = "站点管理", description = "站点信息增删改查API")
public class StationController {

    @Autowired
    private StationService stationService;

    @GetMapping("/page")
    @Operation(summary = "分页查询站点", description = "支持按站点名称和城市筛选的分页查询")
    public JsonResult<IPage<Station>> getStationPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "站点名称") @RequestParam(required = false) String stationName,
            @Parameter(description = "城市") @RequestParam(required = false) String city) {
        try {
            Page<Station> page = new Page<>(current, size);
            IPage<Station> result = stationService.getStationPage(page, stationName, city);
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("分页查询站点失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @GetMapping("/{siteId}")
    @Operation(summary = "根据ID查询站点", description = "根据站点ID查询站点详细信息")
    public JsonResult<Station> getStationById(
            @Parameter(description = "站点ID") @PathVariable Integer siteId) {
        try {
            Station station = stationService.getStationById(siteId);
            if (station == null) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_NOT_FOUND, "站点不存在"));
            }
            return JsonResult.ok(station);
        } catch (Exception e) {
            log.error("查询站点失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "新增站点", description = "新增站点信息")
    public JsonResult<Station> addStation(@RequestBody Station station) {
        try {
            Station result = stationService.addStation(station);
            if (result == null) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_BAD_REQUEST, "新增站点失败"));
            }
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("新增站点失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @PutMapping
    @Operation(summary = "修改站点", description = "修改站点信息")
    public JsonResult<Station> updateStation(@RequestBody Station station) {
        try {
            Station result = stationService.updateStation(station);
            if (result == null) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_NOT_FOUND, "站点不存在"));
            }
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("修改站点失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @DeleteMapping("/{siteId}")
    @Operation(summary = "删除站点", description = "根据站点ID删除站点（逻辑删除）")
    public JsonResult<Boolean> deleteStation(
            @Parameter(description = "站点ID") @PathVariable Integer siteId) {
        try {
            Boolean result = stationService.deleteStation(siteId);
            if (!result) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_NOT_FOUND, "站点不存在"));
            }
            return JsonResult.ok(true);
        } catch (Exception e) {
            log.error("删除站点失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @GetMapping("/levelStat")
    @Operation(summary = "查询站点等级统计", description = "统计所有站点的等级分布情况")
    public JsonResult<StationLevelStatDTO> getStationLevelStat() {
        try {
            StationLevelStatDTO stat = stationService.getStationLevelStat();
            return JsonResult.ok(stat);
        } catch (Exception e) {
            log.error("查询站点等级统计失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @PostMapping("/validateLevel")
    @Operation(summary = "验证站点等级格式", description = "验证站点等级是否符合格式要求（特级、一级、二级、三级、四级、五级）")
    public JsonResult<StationLevelValidateResultDTO> validateStationLevel(@RequestBody StationLevelValidateDTO validateDTO) {
        try {
            StationLevelValidateResultDTO result = stationService.validateStationLevel(validateDTO);
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("验证站点等级失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @GetMapping("/validateLevel")
    @Operation(summary = "验证站点等级格式（GET）", description = "验证站点等级是否符合格式要求，通过URL参数传递")
    public JsonResult<StationLevelValidateResultDTO> validateStationLevelByGet(
            @Parameter(description = "站点ID") @RequestParam Integer siteId,
            @Parameter(description = "站点等级") @RequestParam String stationLevel) {
        try {
            StationLevelValidateDTO validateDTO = new StationLevelValidateDTO();
            validateDTO.setSiteId(siteId);
            validateDTO.setStationLevel(stationLevel);
            StationLevelValidateResultDTO result = stationService.validateStationLevel(validateDTO);
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("验证站点等级失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

}
