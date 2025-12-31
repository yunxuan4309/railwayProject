package com.homework.railwayproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homework.railwayproject.mapper.HighSpeedPassengerCleanMapper;
import com.homework.railwayproject.pojo.dto.TicketQueryDTO;
import com.homework.railwayproject.pojo.dto.TicketResultDTO;
import com.homework.railwayproject.pojo.entity.HighSpeedPassengerClean;
import com.homework.railwayproject.service.TicketQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class TicketQueryServiceImpl extends ServiceImpl<HighSpeedPassengerCleanMapper, HighSpeedPassengerClean> implements TicketQueryService {

    @Override
    public IPage<TicketResultDTO> queryTickets(TicketQueryDTO queryDTO) {
        LambdaQueryWrapper<HighSpeedPassengerClean> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HighSpeedPassengerClean::getIsDeleted, 0);

        if (queryDTO.getStartDate() != null) {
            wrapper.ge(HighSpeedPassengerClean::getTravelDate, queryDTO.getStartDate());
        }

        if (queryDTO.getEndDate() != null) {
            wrapper.le(HighSpeedPassengerClean::getTravelDate, queryDTO.getEndDate());
        }

        if (queryDTO.getTrainCode() != null) {
            wrapper.eq(HighSpeedPassengerClean::getTrainCode, queryDTO.getTrainCode());
        }

        if (StringUtils.hasText(queryDTO.getOperationLineCode())) {
            wrapper.eq(HighSpeedPassengerClean::getOperationLineCode, queryDTO.getOperationLineCode());
        }

        if (queryDTO.getDepartStationId() != null) {
            wrapper.eq(HighSpeedPassengerClean::getDepartStationId, queryDTO.getDepartStationId());
        }

        if (queryDTO.getArriveStationId() != null) {
            wrapper.eq(HighSpeedPassengerClean::getArriveStationId, queryDTO.getArriveStationId());
        }

        if (StringUtils.hasText(queryDTO.getOriginStation())) {
            wrapper.like(HighSpeedPassengerClean::getOriginStation, queryDTO.getOriginStation());
        }

        if (StringUtils.hasText(queryDTO.getDestStation())) {
            wrapper.like(HighSpeedPassengerClean::getDestStation, queryDTO.getDestStation());
        }

        if (queryDTO.getTicketType() != null) {
            wrapper.eq(HighSpeedPassengerClean::getTicketType, queryDTO.getTicketType());
        }

        if (StringUtils.hasText(queryDTO.getTrainLevelCode())) {
            wrapper.eq(HighSpeedPassengerClean::getTrainLevelCode, queryDTO.getTrainLevelCode());
        }

        if (StringUtils.hasText(queryDTO.getTrainTypeCode())) {
            wrapper.eq(HighSpeedPassengerClean::getTrainTypeCode, queryDTO.getTrainTypeCode());
        }

        wrapper.orderByDesc(HighSpeedPassengerClean::getTravelDate);
        wrapper.orderByDesc(HighSpeedPassengerClean::getDepartTime);

        wrapper.select(
            HighSpeedPassengerClean::getTicketId,
            HighSpeedPassengerClean::getTrainCode,
            HighSpeedPassengerClean::getOriginStation,
            HighSpeedPassengerClean::getDestStation,
            HighSpeedPassengerClean::getDepartTime,
            HighSpeedPassengerClean::getTicketType,
            HighSpeedPassengerClean::getTicketPrice
        );

        Integer current = queryDTO.getCurrent() != null ? queryDTO.getCurrent() : Integer.valueOf(1);
        Integer size = queryDTO.getSize() != null ? queryDTO.getSize() : Integer.valueOf(10);
        Page<HighSpeedPassengerClean> page = new Page<>(current, size);

        IPage<HighSpeedPassengerClean> result = page(page, wrapper);

        Page<TicketResultDTO> resultPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        resultPage.setRecords(result.getRecords().stream().map(entity -> {
            TicketResultDTO dto = new TicketResultDTO();
            dto.setTicketId(entity.getTicketId());
            dto.setTrainCode(entity.getTrainCode());
            dto.setOriginStation(entity.getOriginStation());
            dto.setDestStation(entity.getDestStation());
            dto.setDepartTime(entity.getDepartTime());
            dto.setTicketType(entity.getTicketType());
            dto.setTicketPrice(entity.getTicketPrice());
            return dto;
        }).toList());

        return resultPage;
    }
}
