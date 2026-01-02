package com.homework.railwayproject.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.pojo.dto.TicketQueryDTO;
import com.homework.railwayproject.pojo.dto.TicketResultDTO;
import com.homework.railwayproject.service.TicketQueryService;
import com.homework.railwayproject.web.JsonResult;
import com.homework.railwayproject.web.ServiceCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/ticket")
@Tag(name = "票务组合查询", description = "票务数据组合查询API")
public class TicketQueryController {

    @Autowired
    private TicketQueryService ticketQueryService;
    
    @Autowired
    private com.homework.railwayproject.mapper.HighSpeedPassengerCleanMapper passengerCleanMapper;

    @PostMapping("/query")
    @Operation(summary = "组合查询票务数据", description = "支持按日期范围、车次、站点等条件组合查询")
    public JsonResult<IPage<TicketResultDTO>> queryTickets(@RequestBody TicketQueryDTO queryDTO) {
        log.info("TicketQueryController.queryTickets() - 接收到的查询参数: {}", queryDTO);
        try {
            IPage<TicketResultDTO> result = ticketQueryService.queryTickets(queryDTO);
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("组合查询票务数据失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @GetMapping("/ticket-types")
    @Operation(summary = "查询所有车票类型", description = "返回所有可用的车票类型")
    public JsonResult<List<Integer>> getTicketTypes() {
        try {
            List<Integer> ticketTypes = passengerCleanMapper.selectDistinctTicketTypes();
            return JsonResult.ok(ticketTypes);
        } catch (Exception e) {
            log.error("查询车票类型失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @GetMapping("/seat-types")
    @Operation(summary = "查询所有座位类型", description = "返回所有可用的座位类型")
    public JsonResult<List<String>> getSeatTypes() {
        try {
            List<String> seatTypes = passengerCleanMapper.selectDistinctSeatTypes();
            return JsonResult.ok(seatTypes);
        } catch (Exception e) {
            log.error("查询座位类型失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }

    @GetMapping("/train-types")
    @Operation(summary = "查询所有列车类型", description = "返回所有可用的列车类型分类（高铁/城际/普速）")
    public JsonResult<List<String>> getTrainTypes() {
        try {
            // 返回固定的列车类型分类
            List<String> trainTypes = List.of("高铁", "城际", "普速");
            return JsonResult.ok(trainTypes);
        } catch (Exception e) {
            log.error("查询列车类型失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }
    
    @GetMapping("/train-level-codes")
    @Operation(summary = "查询所有列车等级码", description = "返回数据库中所有实际的列车等级码，用于调试trainType查询")
    public JsonResult<List<String>> getTrainLevelCodes() {
        try {
            List<String> trainLevelCodes = passengerCleanMapper.selectDistinctTrainLevelTypes();
            return JsonResult.ok(trainLevelCodes);
        } catch (Exception e) {
            log.error("查询列车等级码失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }
}