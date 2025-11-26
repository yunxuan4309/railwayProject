package com.homework.railwayproject.pojo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

//Author:[谢云轩]
//QQ:[1721476339]
//ID:[632307060623]
//Date:2025/11/18
//Time:20:36
@Data
public class HighSpeedPassenger extends BaseEntity{//这个类是原始数据类,仅用于辅助数据导入,之后还会有清洗数据类
    private Long id;                  // 序号
    private String lineCode;          // 运营线路编码
    private Integer trainCode;        // 列车编码
    private Integer siteId;           // 站点id
    private Integer lineSiteId;       // 线路站点id
    private String uplineCode;        // 上行线编码
    private LocalDate operationDate;       // 运行日期
    private String operationTime;     // 运行时间
    private Integer distanceOrder;    // 与起点站距序
    private Integer isStartSite;      // 是否起始站点
    private Integer isEndSite;        // 是否终点站点

    private String arrivalTime;       // 到达时间
    private String departureTime;     // 出发时间
    private String stopTime;          // 经停时间

    private Integer passengerFlow;    // 客流量
    private Integer uplinePassengerFlow;  // 上行客流量
    private Integer downlinePassengerFlow; // 下行客流量
    private Integer boardingCount;    // 上客量
    private Integer alightingCount;   // 下客量
    private String remarks;           // 备注
    private LocalDate trainDepartureDate;  // 列车出发日期

    private String trainDepartureTime; // 列出出发时间

    private Integer siteSequence;     // 站点序号
    private Integer ticketType;       // 车票类型
    private BigDecimal ticketPrice;   // 车票价格
    private String seatTypeCode;      // 座位类型编码
    private String trainCompanyCode;  // 列车公司编码
    private LocalDate startDate;           // 开始日期
    private String startStationTelecode; // 起点站电报码
    private String startStation;      // 起点站
    private String endStationTelecode; // 终到站电报码
    private String endStation;        // 终到站
    private String trainLevelCode;    // 列车等级码
    private String trainTypeCode;     // 列车类型码
    private String ticketStation;     // 售票站
    private String farthestArrivalStation; // 最远到达站
    private LocalDate ticketTime;          // 售票时间
    private String arrivalStation;    // 到达站
    private BigDecimal revenue;       // 收入


}