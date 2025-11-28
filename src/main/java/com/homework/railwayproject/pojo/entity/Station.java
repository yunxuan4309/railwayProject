package com.homework.railwayproject.pojo.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

//Author:[谢云轩]
//QQ:[1721476339]
//ID:[632307060623]
//Date:2025/11/18
//Time:20:29
@Data
public class Station extends BaseEntity implements Serializable {
    private Integer siteId;           // 站点id
    private Integer typeId;           // 类型id
    private Integer transportModeCode; // 运输方式编码
    private String stationName;       // 站点名称
    private Integer isDisabled;       // 是否停用
    private String siteCode;          // 站点code
    private String stationTelecode;   // 站点电报码
    private String stationAlias;      // 站点小名
    private BigDecimal longitude;     // 经度
    private BigDecimal latitude;      // 纬度
    private String stationLevel;      // 站点等级
    private Integer platformCount;    // 站台数量
    private Integer gateCount;        // 检票口数量
    private String city;              // 所属城市


}