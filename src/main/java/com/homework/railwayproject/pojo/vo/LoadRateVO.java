package com.homework.railwayproject.pojo.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LoadRateVO {
    private String lineCode;           // 线路编码
    private Integer startStationId;    // 起始站ID
    private Integer endStationId;      // 终点站ID
    private String startStationName;   // 起始站名称
    private String endStationName;     // 终点站名称
    private LocalDate flowDate;        // 统计日期
    private Integer hour;              // 小时
    private String timeRange;          // 时间范围（如：08:00-08:59）
    private Integer passengerCount;    // 客流量
    private Integer trainCapacity;     // 列车总运力
    private Double loadRate;           // 满载率
    private Integer trainCode;         // 列车编码
    private String trainType;          // 列车类型
    private String trainNumber;        // 车次
    private Integer boardingCount;     // 上客量
    private Integer alightingCount;    // 下客量
}