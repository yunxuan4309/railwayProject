package com.homework.railwayproject.pojo.dto;

import lombok.Data;

@Data
public class StationLevelValidateResultDTO {
    private Integer siteId;
    private String stationName;
    private String stationLevel;
    private Boolean isValid;
    private String message;
}
