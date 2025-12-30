package com.homework.railwayproject.service;

import com.homework.railwayproject.pojo.entity.SensitivityConfig;

/**
 * 灵敏度配置服务接口
 */
public interface SensitivityConfigService {

    /**
     * 根据配置类型获取灵敏度配置
     *
     * @param configType 配置类型
     * @return 灵敏度配置
     */
    SensitivityConfig getSensitivityByConfigType(String configType);

    /**
     * 根据配置类型更新灵敏度值
     *
     * @param configType 配置类型
     * @param sensitivityValue 灵敏度值
     * @return 更新成功返回true，否则返回false
     */
    boolean updateSensitivityByConfigType(String configType, Double sensitivityValue);

    /**
     * 获取高峰时段统计的灵敏度配置
     *
     * @return 灵敏度值
     */
    Double getPeakHourSensitivity();

    /**
     * 更新高峰时段统计的灵敏度配置
     *
     * @param sensitivityValue 灵敏度值
     * @return 更新成功返回true，否则返回false
     */
    boolean updatePeakHourSensitivity(Double sensitivityValue);
}