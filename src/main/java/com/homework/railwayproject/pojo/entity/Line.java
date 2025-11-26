package com.homework.railwayproject.pojo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//Author:[谢云轩]
//QQ:[1721476339]
//ID:[632307060623]
//Date:2025/11/18
//Time:20:35
@Data
public class Line extends BaseEntity{
    //补充线路表
    private String lineCode;          // 线路编码
    private String lineName;          // 线路名称
    private String lineType;          // 线路类型
    private Integer startStationId;   // 起始站id
    private Integer endStationId;     // 终点站id
    private BigDecimal totalMileage;  // 总里程
    private String operationStatus;   // 运营状态


}