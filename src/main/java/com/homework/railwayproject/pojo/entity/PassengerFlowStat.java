package com.homework.railwayproject.pojo.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 日客流统计数据实体类
 */
@Setter
@Getter
@Data
public class PassengerFlowStat {
    /**
     * 统计开始日期
     */
    private LocalDate beginDate;
    /**
     * 统计结束日期
     */
    private LocalDate endDate;

    /**
     * 统计周期类型 (daily, weekly, monthly)
     */
    private String periodType;

    /**
     * 客流量
     */
    private Integer passengerFlow;

    /**
     * 环比增长率 (%)
     */
    private BigDecimal comparisonRate;

    /**
     * 同比增长率 (%)
     */
    private BigDecimal yearOnYearRate;

    /**
     * 是否为节假日 (0-否, 1-是)
     */
    private Integer isHoliday = 0;
}