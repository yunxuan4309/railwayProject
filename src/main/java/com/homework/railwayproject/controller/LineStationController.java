package com.homework.railwayproject.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.pojo.entity.LineStation;
import com.homework.railwayproject.service.LineStationService;
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
@RequestMapping("/api/line-station")
@Tag(name = "线路站点管理", description = "线路站点关系增删改查API")
public class LineStationController {

    @Autowired
    private LineStationService lineStationService;

    @GetMapping("/page")
    @Operation(summary = "分页查询线路站点关系", description = "支持按线路编码和站点ID筛选的分页查询")
    public JsonResult<IPage<LineStation>> getLineStationPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "线路编码") @RequestParam(required = false) String lineCode,
            @Parameter(description = "站点ID") @RequestParam(required = false) Integer siteId) {
        try {
            Page<LineStation> page = new Page<>(current, size);
            IPage<LineStation> result = lineStationService.getLineStationPage(page, lineCode, siteId);
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("分页查询线路站点关系失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询线路站点关系", description = "根据ID查询线路站点关系详细信息")
    public JsonResult<LineStation> getLineStationById(
            @Parameter(description = "ID") @PathVariable Long id) {
        try {
            LineStation lineStation = lineStationService.getLineStationById(id);
            if (lineStation == null) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_NOT_FOUND, "线路站点关系不存在"));
            }
            return JsonResult.ok(lineStation);
        } catch (Exception e) {
            log.error("查询线路站点关系失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "新增线路站点关系", description = "新增线路站点关系信息")
    public JsonResult<LineStation> addLineStation(@RequestBody LineStation lineStation) {
        try {
            LineStation result = lineStationService.addLineStation(lineStation);
            if (result == null) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_BAD_REQUEST, "新增线路站点关系失败"));
            }
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("新增线路站点关系失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @PutMapping
    @Operation(summary = "修改线路站点关系", description = "修改线路站点关系信息")
    public JsonResult<LineStation> updateLineStation(@RequestBody LineStation lineStation) {
        try {
            LineStation result = lineStationService.updateLineStation(lineStation);
            if (result == null) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_NOT_FOUND, "线路站点关系不存在"));
            }
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("修改线路站点关系失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除线路站点关系", description = "根据ID删除线路站点关系（逻辑删除）")
    public JsonResult<Boolean> deleteLineStation(
            @Parameter(description = "ID") @PathVariable Long id) {
        try {
            Boolean result = lineStationService.deleteLineStation(id);
            if (!result) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_NOT_FOUND, "线路站点关系不存在"));
            }
            return JsonResult.ok(true);
        } catch (Exception e) {
            log.error("删除线路站点关系失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }
}