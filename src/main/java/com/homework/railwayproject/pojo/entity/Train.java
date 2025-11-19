package com.homework.railwayproject.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

//Author:[谢云轩]
//QQ:[1721476339]
//ID:[632307060623]
//Date:2025/11/18
//Time:20:34
@Data
public class Train extends BaseEntity{
    private Integer trainCode;        // 列车编码
    private String uplineCode;        // 上行线编码
    private Integer transportModeCode; // 运输方式编码
    private String trainNumber;       // 列车代码
    private String trainId;           // 车次
    private Integer isOfficial;       // 是否正图
    private Integer trainCapacity;    // 列车运量

}