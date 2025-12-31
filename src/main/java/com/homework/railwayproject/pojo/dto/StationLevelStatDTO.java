package com.homework.railwayproject.pojo.dto;

import lombok.Data;

import java.util.Map;

@Data
public class StationLevelStatDTO {
    private Long totalStations;
    private Map<String, Long> levelDistribution;
}
