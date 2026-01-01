package com.homework.railwayproject.pojo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SectionLoadRateQueryDTO {
    private String lineCode;      // 线路编码
    private LocalDate flowDate;   // 查询日期
    private Integer startHour;    // 开始小时（可选）
    private Integer endHour;      // 结束小时（可选）
}