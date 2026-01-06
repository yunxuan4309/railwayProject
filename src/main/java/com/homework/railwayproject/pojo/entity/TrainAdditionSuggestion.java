package com.homework.railwayproject.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * 列车增车建议实体类
 */
@Data
@TableName("train_addition_suggestion")
public class TrainAdditionSuggestion {
    @TableId(type = IdType.AUTO)
    private Long id;                    // 主键ID

    private String lineCode;            // 线路编码
    private String section;             // 需要加车的区间
    private String suggestedTrainNumber; // 建议车次号
    private LocalTime departureTime;    // 建议发车时间
    private LocalTime arrivalTime;      // 建议到达时间
    private Integer carriageCount;      // 建议编组
    private String trainType;           // 建议车型
    private String reason;              // 建议原因
    private Double expectedLoadRate;    // 预计满载率
    private String status;              // 建议状态：PENDING-待处理，APPROVED-已批准，REJECTED-已拒绝
    private String createdBy;           // 建议创建者类型：SYSTEM-系统生成，MANUAL-人工添加
    private LocalDate suggestDate;      // 建议生成日期

    // 基础字段
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createBy;
    private String updateBy;
    private Integer isDeleted = 0;
}