package com.homework.railwayproject.pojo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LineSectionLoadRateQueryDTO {
    private String lineCode;          // 线路编码
    private LocalDate flowDate;       // 统计日期
    private LocalDate startDate;      // 开始日期
    private LocalDate endDate;        // 结束日期
    private Integer startHour;        // 开始小时
    private Integer endHour;          // 结束小时
    private Integer startStationId;   // 起始站ID
    private Integer endStationId;     // 终点站ID
    private String startStationName;  // 起始站名称
    private String endStationName;    // 终点站名称
    private Integer page = 1;         // 页码，默认为1
    private Integer size = 10;        // 每页大小，默认为10
}