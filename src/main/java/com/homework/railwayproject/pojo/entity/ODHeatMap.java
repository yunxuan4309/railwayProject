package com.homework.railwayproject.pojo.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * OD热力图实体类
 * 用于承载OD（起始点-目的地）客流热力图的核心数据
 * 
 * 用途说明：
 * - originStationId/destStationId: 站点唯一标识，用于数据关联和前端展示
 * - originStationName/destStationName: 站点名称，用于前端界面展示
 * - passengerFlow: 客流量（上客量+下客量），用于热力图颜色深浅、桑基图线宽等视觉编码
 * - travelDate: 乘车日期，用于前端显示和数据分组
 * - heatValue: 热力值（该OD对客流量/总客流量），用于归一化显示不同日期间的相对热度
 * - rank: 排名（按客流量排序），用于前端数据排序和优先级展示
 */
@Data
public class ODHeatMap {

    /**
     * 起始站点ID
     * 用途：用于数据关联和前端展示
     */
    private Integer originStationId;

    /**
     * 起始站点名称
     * 用途：用于前端界面展示
     */
    private String originStationName;

    /**
     * 目标站点ID
     * 用途：用于数据关联和前端展示
     */
    private Integer destStationId;

    /**
     * 目标站点名称
     * 用途：用于前端界面展示
     */
    private String destStationName;

    /**
     * 客流量（上客量+下客量）
     * 用途：用于热力图颜色深浅、桑基图线宽、关系图连线粗细等视觉编码
     */
    private Integer passengerFlow;

    /**
     * 乘车日期
     * 用途：用于前端显示和数据分组
     */
    private String travelDate;

    /**
     * 热力值
     * 计算方式：该OD对客流量 / 总客流量
     * 用途：用于归一化显示不同日期间的相对热度，便于跨日期比较
     */
    private BigDecimal heatValue;

    /**
     * 排名
     * 计算方式：按客流量降序排序
     * 用途：用于前端数据排序和优先级展示
     */
    private Integer rank;
}