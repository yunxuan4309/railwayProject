package com.homework.railwayproject.pojo.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

//Author:[谢云轩]
//QQ:[1721476339]
//ID:[632307060623]
//Date:2025/11/18
//Time:20:35
@Data
public class LineStation extends BaseEntity implements Serializable {
    private String lineCode;          // 运营线路编码
    private Integer siteId;           // 站点id
    private Integer lineSiteId;       // 线路站点id
    private Integer previousSiteId;   // 上一站id
    private BigDecimal lineDistance;  // 运营线路站间距离
    private Integer nextSiteId;       // 下一站id
    private Integer isStartStation;   // 是否起始站点
    private Integer isEndStation;     // 是否终点站点
    private BigDecimal transportDistance; // 运输距离
    private String lineNumber;        // 线路代码
    private Integer needStop;         // 是否要停靠


}