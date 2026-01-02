package com.homework.railwayproject.pojo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TicketQueryDTO {

    private LocalDate startDate;

    private LocalDate endDate;

    private String trainNumber; // 替换trainCode，用于查询列车车次

    private String originStation;

    private String destStation;

    private Integer ticketType;

    private String trainType; // 替换trainLevelCode，用于查询列车类型（高铁/城际/普速）

    private String trainTypeCode;

    private String seatTypeCode; // 新增座位类型查询

    private Integer current;

    private Integer size;
}