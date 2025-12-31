package com.homework.railwayproject.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * OD热力图数据传输对象
 * 用于向前端传输OD热力图数据，提供结构化的数据格式
 * 
 * 用途说明：
 * - originStation/destStation: 站点信息，包含ID和名称，用于前端展示
 * - passengerFlow: 客流量，用于热力图颜色深浅、桑基图线宽等视觉编码
 * - dateRange: 日期范围，用于前端显示
 * - heatValue: 热力值，用于归一化显示不同日期间的相对热度
 * - ODHeatMapListDTO: 批量返回数据的容器，包含额外的元数据
 */
@Data
public class ODHeatMapDTO {

    /**
     * 起始站点信息
     * 用途：用于前端展示起始站点的ID和名称
     */
    private StationInfo originStation;

    /**
     * 目标站点信息
     * 用途：用于前端展示目标站点的ID和名称
     */
    private StationInfo destStation;

    /**
     * 客流量
     * 用途：用于热力图颜色深浅、桑基图线宽等视觉编码
     */
    private Integer passengerFlow;

    /**
     * 时间范围
     * 用途：用于前端显示和数据分组
     */
    private String dateRange;

    /**
     * 热力值（用于可视化权重）
     * 计算方式：该OD对客流量 / 总客流量
     * 用途：用于归一化显示，便于跨日期比较
     */
    private BigDecimal heatValue;

    /**
     * 站点信息内部类
     * 用途：封装站点的基本信息
     */
    @Data
    public static class StationInfo {
        /**
         * 站点ID
         * 用途：用于数据关联和唯一标识
         */
        private Integer stationId;
        
        /**
         * 站点名称
         * 用途：用于前端界面展示
         */
        private String stationName;
    }

    /**
     * 用于批量返回的集合DTO
     * 用途：提供结构化的批量数据返回格式，包含额外的元数据
     */
    @Data
    public static class ODHeatMapListDTO {
        /**
         * OD数据列表
         * 用途：包含多个OD热力图数据项
         */
        private List<ODHeatMapDTO> odData;
        
        /**
         * 日期范围
         * 用途：用于前端显示查询的时间范围
         */
        private String dateRange;
        
        /**
         * 总记录数
         * 用途：用于前端分页或统计显示
         */
        private Integer totalRecords;
    }
}