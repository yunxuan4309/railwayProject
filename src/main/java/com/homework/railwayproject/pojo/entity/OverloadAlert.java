package com.homework.railwayproject.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("overload_alert")
public class OverloadAlert {
    @TableId(type = IdType.AUTO)
    private Long id;                    // 主键ID

    private String lineCode;            // 线路编码
    private Integer startStationId;     // 起始站ID
    private Integer endStationId;       // 终点站ID
    private LocalDate alertStartDate;   // 告警开始日期
    private LocalDate alertEndDate;     // 告警结束日期
    private Integer consecutiveDays;    // 连续天数
    private Double avgLoadRate;         // 平均满载率
    private String alertLevel = "HIGH"; // 告警级别
    private String status = "ACTIVE";   // 状态

    // 基础字段
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createBy;
    private String updateBy;
    private Integer isDeleted = 0;
}