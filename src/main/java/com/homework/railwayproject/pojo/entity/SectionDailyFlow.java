package com.homework.railwayproject.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("section_daily_flow")
public class SectionDailyFlow {
    @TableId(type = IdType.AUTO)
    private Long id;                    // 主键ID

    private String lineCode;            // 线路编码
    private Integer startStationId;     // 起始站ID
    private Integer endStationId;       // 终点站ID
    private LocalDate flowDate;         // 统计日期
    private Double avgLoadRate;         // 平均满载率
    private Double maxLoadRate;         // 最高满载率

    // 基础字段
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createBy;
    private String updateBy;
    private Integer status = 1;
    private Integer isDeleted = 0;
}