package com.homework.railwayproject.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.pojo.entity.Line;
import com.homework.railwayproject.service.LineService;
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
@RequestMapping("/api/line")
@Tag(name = "线路管理", description = "线路信息增删改查API")
public class LineController {

    @Autowired
    private LineService lineService;

    @GetMapping("/page")
    @Operation(summary = "分页查询线路", description = "支持按线路名称和类型筛选的分页查询")
    public JsonResult<IPage<Line>> getLinePage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "线路名称") @RequestParam(required = false) String lineName,
            @Parameter(description = "线路类型") @RequestParam(required = false) String lineType) {
        try {
            Page<Line> page = new Page<>(current, size);
            IPage<Line> result = lineService.getLinePage(page, lineName, lineType);
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("分页查询线路失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @GetMapping("/{lineCode}")
    @Operation(summary = "根据线路编码查询线路", description = "根据线路编码查询线路详细信息")
    public JsonResult<Line> getLineByCode(
            @Parameter(description = "线路编码") @PathVariable String lineCode) {
        try {
            Line line = lineService.getLineByCode(lineCode);
            if (line == null) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_NOT_FOUND, "线路不存在"));
            }
            return JsonResult.ok(line);
        } catch (Exception e) {
            log.error("查询线路失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "新增线路", description = "新增线路信息")
    public JsonResult<Line> addLine(@RequestBody Line line) {
        try {
            Line result = lineService.addLine(line);
            if (result == null) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_BAD_REQUEST, "新增线路失败"));
            }
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("新增线路失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @PutMapping
    @Operation(summary = "修改线路", description = "修改线路信息")
    public JsonResult<Line> updateLine(@RequestBody Line line) {
        try {
            Line result = lineService.updateLine(line);
            if (result == null) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_NOT_FOUND, "线路不存在"));
            }
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("修改线路失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @DeleteMapping("/{lineCode}")
    @Operation(summary = "删除线路", description = "根据线路编码删除线路（逻辑删除）")
    public JsonResult<Boolean> deleteLine(
            @Parameter(description = "线路编码") @PathVariable String lineCode) {
        try {
            Boolean result = lineService.deleteLine(lineCode);
            if (!result) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_NOT_FOUND, "线路不存在"));
            }
            return JsonResult.ok(true);
        } catch (Exception e) {
            log.error("删除线路失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }
}
