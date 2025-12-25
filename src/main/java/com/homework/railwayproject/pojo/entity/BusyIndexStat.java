package com.homework.railwayproject.pojo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 繁忙指数统计实体类
 * 包含站点信息以及发送量、到达量和繁忙指数
 */
@Data
public class BusyIndexStat implements Serializable {

    /**
     * 站点ID
     */
    private Integer siteId;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 发送量（上客量）
     */
    private Integer departureCount;

    /**
     * 到达量（下客量）
     */
    private Integer arrivalCount;

    /**
     * 繁忙指数 = 发送量 * 0.4 + 到达量 * 0.6
     */
    private Double busyIndex;

    /**
     * 站点排名
     */
    private Integer rank;
}