package com.homework.railwayproject.pojo.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OverloadAlertVO {
    private String lineCode;           // 线路编码
    private Integer startStationId;    // 起始站ID
    private Integer endStationId;      // 终点站ID
    private String startStationName;   // 起始站名称
    private String endStationName;     // 终点站名称
    private String section;            // 区间（如：北京南-天津西）
    private LocalDate alertStartDate;  // 告警开始日期
    private LocalDate alertEndDate;    // 告警结束日期
    private Integer consecutiveDays;   // 连续天数
    private Double avgLoadRate;        // 平均满载率
    private String alertLevel;         // 告警级别
    private String status;             // 状态
}