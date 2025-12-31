package com.homework.railwayproject.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.pojo.entity.Train;
import com.homework.railwayproject.service.TrainService;
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
@RequestMapping("/api/train")
@Tag(name = "列车管理", description = "列车信息增删改查API")
public class TrainController {

    @Autowired
    private TrainService trainService;

    @GetMapping("/page")
    @Operation(summary = "分页查询列车", description = "支持按列车代码和车次筛选的分页查询")
    public JsonResult<IPage<Train>> getTrainPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "列车代码") @RequestParam(required = false) String trainNumber,
            @Parameter(description = "车次") @RequestParam(required = false) String trainId) {
        try {
            Page<Train> page = new Page<>(current, size);
            IPage<Train> result = trainService.getTrainPage(page, trainNumber, trainId);
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("分页查询列车失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @GetMapping("/{trainCode}")
    @Operation(summary = "根据列车编码查询列车", description = "根据列车编码查询列车详细信息")
    public JsonResult<Train> getTrainByCode(
            @Parameter(description = "列车编码") @PathVariable Integer trainCode) {
        try {
            Train train = trainService.getTrainByCode(trainCode);
            if (train == null) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_NOT_FOUND, "列车不存在"));
            }
            return JsonResult.ok(train);
        } catch (Exception e) {
            log.error("查询列车失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "新增列车", description = "新增列车信息")
    public JsonResult<Train> addTrain(@RequestBody Train train) {
        try {
            Train result = trainService.addTrain(train);
            if (result == null) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_BAD_REQUEST, "新增列车失败"));
            }
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("新增列车失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @PutMapping
    @Operation(summary = "修改列车", description = "修改列车信息")
    public JsonResult<Train> updateTrain(@RequestBody Train train) {
        try {
            Train result = trainService.updateTrain(train);
            if (result == null) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_NOT_FOUND, "列车不存在"));
            }
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("修改列车失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @DeleteMapping("/{trainCode}")
    @Operation(summary = "删除列车", description = "根据列车编码删除列车（逻辑删除）")
    public JsonResult<Boolean> deleteTrain(
            @Parameter(description = "列车编码") @PathVariable Integer trainCode) {
        try {
            Boolean result = trainService.deleteTrain(trainCode);
            if (!result) {
                return JsonResult.fail(new ServiceException(ServiceCode.ERROR_NOT_FOUND, "列车不存在"));
            }
            return JsonResult.ok(true);
        } catch (Exception e) {
            log.error("删除列车失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }
}
