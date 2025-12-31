package com.homework.railwayproject.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 站点等级容量配置实体类
 * 用于存储不同等级站点的容客量标准，支持动态配置
 */
@Data
@TableName("station_level_capacity_config")
public class StationLevelCapacityConfig extends BaseEntity implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 站点等级（如：特等、一等、二等、三等、四等、五等）
     */
    private String stationLevel;

    /**
     * 站点等级名称（如：特等站、一等站等）
     */
    private String levelName;

    /**
     * 基础容客量（每小时最大容纳客流量）
     */
    private Integer baseCapacity;

    /**
     * 每个站台的额外容客量
     */
    private Integer platformCapacity;

    /**
     * 每个检票口的额外容客量
     */
    private Integer gateCapacity;

    /**
     * 是否启用（0-禁用，1-启用）
     */
    private Integer enabled;
}