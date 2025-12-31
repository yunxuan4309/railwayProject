package com.homework.railwayproject.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 灵敏度配置实体类
 * 该配置可用于繁忙指数的权重配置和高峰时段灵敏度配置，范围均在0到1之间
 */
@Data
@TableName("sensitivity_config")
public class SensitivityConfig extends BaseEntity implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置类型（如：peak_hour_sensitivity, busy_index_sensitivity等）
     */
    private String configType;

    /**
     * 数值（对于容量配置为整数值，对于灵敏度配置为0.00-1.00之间的值）
     * 该配置可用于繁忙指数的权重配置、高峰时段灵敏度配置或容量基础值配置
     */
    private Double sensitivityValue;

    /**
     * 配置描述
     */
    private String description;
}