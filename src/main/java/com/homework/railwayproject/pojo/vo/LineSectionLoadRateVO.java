package com.homework.railwayproject.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LineSectionLoadRateVO {
    private String lineCode;           // 线路编码
    private String lineName;           // 线路名称
    private Integer startStationId;    // 起始站ID
    private String startStationName;   // 起始站名称
    private BigDecimal startLongitude; // 起始站经度
    private BigDecimal startLatitude;  // 起始站纬度
    private Integer endStationId;      // 终点站ID
    private String endStationName;     // 终点站名称
    private BigDecimal endLongitude;   // 终点站经度
    private BigDecimal endLatitude;    // 终点站纬度
    private LocalDate flowDate;        // 统计日期
    private Integer hour;              // 小时
    private String timeRange;          // 时间范围（如：08:00-08:59）
    private Integer passengerCount;    // 客流量
    private Integer trainCapacity;     // 列车总运力
    private Double loadRate;           // 满载率
    private String lineType;           // 线路类型
}