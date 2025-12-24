package com.homework.railwayproject.pojo.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 城市路线热度实体类
 * 表示两个城市之间的客流热度值
 */
@Data
public class CityRouteHeat implements Serializable {
    
    /**
     * 出发城市
     */
    private String originCity;
    
    /**
     * 到达城市
     */
    private String destCity;
    
    /**
     * 日客流量
     */
    private Integer dailyPassengerFlow;
    
    /**
     * 线路总客流量
     */
    private Integer totalLinePassengerFlow;
    
    /**
     * 热度值 = 日客流 / 线路总客流
     */
    private BigDecimal heatValue;
    
    /**
     * 排名
     */
    private Integer rank;
}