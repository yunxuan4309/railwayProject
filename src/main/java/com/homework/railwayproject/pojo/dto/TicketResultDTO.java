package com.homework.railwayproject.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class TicketResultDTO {

    private String ticketId;

    private Integer trainCode;

    private String trainNumber; // 新增：车次

    private String originStation;

    private String destStation;

    private LocalTime departTime;

    private Integer ticketType;

    private String seatTypeCode; // 新增：座位类型

    private String trainCompanyCode; // 新增：列车公司码

    private BigDecimal ticketPrice;
}