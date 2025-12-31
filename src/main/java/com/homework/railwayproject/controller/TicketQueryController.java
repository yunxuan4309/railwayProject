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

@Slf4j
@RestController
@RequestMapping("/api/ticket")
@Tag(name = "票务组合查询", description = "票务数据组合查询API")
public class TicketQueryController {

    @Autowired
    private TicketQueryService ticketQueryService;

    @PostMapping("/query")
    @Operation(summary = "组合查询票务数据", description = "支持按日期范围、车次、站点等条件组合查询")
    public JsonResult<IPage<TicketResultDTO>> queryTickets(@RequestBody TicketQueryDTO queryDTO) {
        try {
            IPage<TicketResultDTO> result = ticketQueryService.queryTickets(queryDTO);
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("组合查询票务数据失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, e.getMessage()));
        }
    }
}
