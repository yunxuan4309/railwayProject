package com.homework.railwayproject.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homework.railwayproject.mapper.HighSpeedPassengerCleanMapper;
import com.homework.railwayproject.pojo.dto.TicketQueryDTO;
import com.homework.railwayproject.pojo.dto.TicketResultDTO;
import com.homework.railwayproject.pojo.entity.HighSpeedPassengerClean;
import com.homework.railwayproject.service.TicketQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class TicketQueryServiceImpl extends ServiceImpl<HighSpeedPassengerCleanMapper, HighSpeedPassengerClean> implements TicketQueryService {

    @Autowired
    private HighSpeedPassengerCleanMapper passengerCleanMapper;

    @Override
    public IPage<TicketResultDTO> queryTickets(TicketQueryDTO queryDTO) {
        // 添加调试日志
        log.info("TicketQueryServiceImpl.queryTickets() - 查询参数: {}", queryDTO);
        
        if (queryDTO.getStartDate() != null) {
            log.info("开始日期: {}", queryDTO.getStartDate());
        }
        if (queryDTO.getEndDate() != null) {
            log.info("结束日期: {}", queryDTO.getEndDate());
        }
        if (queryDTO.getTrainType() != null) {
            log.info("列车类型: {}", queryDTO.getTrainType());
        }
        
        // 构建查询条件
        Integer current = queryDTO.getCurrent() != null ? queryDTO.getCurrent() : Integer.valueOf(1);
        Integer size = queryDTO.getSize() != null ? queryDTO.getSize() : Integer.valueOf(10);
        Page<HighSpeedPassengerClean> page = new Page<>(current, size);

        // 调用Mapper方法执行自定义查询
        IPage<TicketResultDTO> result = passengerCleanMapper.queryTicketsWithConditions(page, queryDTO);
        
        log.info("查询结果总数: {}", result.getTotal());
        log.info("查询结果记录数: {}", result.getRecords().size());

        return result;
    }
}