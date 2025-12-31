package com.homework.railwayproject.pojo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TicketQueryDTO {

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer trainCode;

    private String operationLineCode;

    private Integer departStationId;

    private Integer arriveStationId;

    private String originStation;

    private String destStation;

    private Integer ticketType;

    private String trainLevelCode;

    private String trainTypeCode;

    private Integer current;

    private Integer size;
}
