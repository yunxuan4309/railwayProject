package com.homework.railwayproject.pojo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 站点客流统计实体类
 * 包含站点信息以及上客量、下客量、总客量和排名
 */
@Data
public class StationPassengerFlowStat implements Serializable {
    
    /**
     * 站点ID
     */
    private Integer siteId;
    
    /**
     * 站点名称
     */
    private String siteName;
    
    /**
     * 上客量
     */
    private Integer boardingCount;
    
    /**
     * 下客量
     */
    private Integer alightingCount;
    
    /**
     * 总客量
     */
    private Integer totalPassengerCount;
    
    /**
     * 站点排名
     */
    private Integer siteTop;
}