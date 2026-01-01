package com.homework.railwayproject.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("section_hourly_flow")
public class SectionHourlyFlow {
    @TableId(type = IdType.AUTO)
    private Long id;                    // 主键ID，使用 @TableId 注解

    private String lineCode;            // 线路编码
    private Integer startStationId;     // 起始站ID
    private Integer endStationId;       // 终点站ID
    private LocalDate flowDate;         // 统计日期
    private Integer hour;               // 小时 (0-23)
    private Integer passengerCount;     // 客流量
    private Integer trainCapacity;      // 列车总运力
    private Double loadRate;            // 满载率

    // 基础字段（与 BaseEntity 保持一致，但不继承）
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createBy;
    private String updateBy;
    private Integer status = 1;
    private Integer isDeleted = 0;
}