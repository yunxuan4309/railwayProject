package com.homework.railwayproject.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class TicketResultDTO {

    private String ticketId;

    private Integer trainCode;

    private String originStation;

    private String destStation;

    private LocalTime departTime;

    private Integer ticketType;

    private BigDecimal ticketPrice;
}
