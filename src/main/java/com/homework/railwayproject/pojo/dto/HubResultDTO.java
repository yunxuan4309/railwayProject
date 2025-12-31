package com.homework.railwayproject.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HubResultDTO {
    private Integer siteId;
    private String stationName;
    private String stationLevel;
    private Integer degreeCentrality;
    private BigDecimal betweennessCentrality;
    private String hubLevel;
    private String trainType;
}
