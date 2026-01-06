package com.homework.railwayproject.pojo.dto;

import lombok.Data;

/**
 * 列车增车建议查询DTO
 */
@Data
public class TrainAdditionSuggestionQueryDTO {
    private String lineCode;            // 线路编码
    private String section;             // 区间
    private String status;              // 建议状态
    private String createdBy;           // 建议创建者类型：SYSTEM-系统生成，MANUAL-人工添加
    private Integer current = 1;           // 当前页码
    private Integer size = 10;          // 每页大小
}