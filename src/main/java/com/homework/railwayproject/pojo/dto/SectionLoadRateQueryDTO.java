package com.homework.railwayproject.pojo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SectionLoadRateQueryDTO {
    private String lineCode;      // 线路编码
    private LocalDate flowDate;   // 查询日期
    private Integer startHour;    // 开始小时（可选）
    private Integer endHour;      // 结束小时（可选）
    private Integer page;         // 页码，默认为1
    private Integer size;         // 每页大小，默认为10
    
    // 新增站点查询字段
    private Integer startStationId;  // 起始站ID（可选）
    private Integer endStationId;    // 终点站ID（可选）
    private String startStationName; // 起始站名称（可选）
    private String endStationName;   // 终点站名称（可选）
    private String section;          // 区间（可选，格式：起始站名-终点站名）
}