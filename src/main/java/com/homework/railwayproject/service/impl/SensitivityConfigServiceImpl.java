package com.homework.railwayproject.service.impl;

import com.homework.railwayproject.mapper.SensitivityConfigMapper;
import com.homework.railwayproject.pojo.entity.SensitivityConfig;
import com.homework.railwayproject.service.SensitivityConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 灵敏度配置服务实现类
 */
@Slf4j
@Service
public class SensitivityConfigServiceImpl implements SensitivityConfigService {

    @Autowired
    private SensitivityConfigMapper sensitivityConfigMapper;

    @Override
    public SensitivityConfig getSensitivityByConfigType(String configType) {
        log.info("获取配置类型为 {} 的灵敏度配置", configType);
        return sensitivityConfigMapper.selectByConfigType(configType);
    }

    @Override
    public boolean updateSensitivityByConfigType(String configType, Double sensitivityValue) {
        log.info("更新配置类型为 {} 的灵敏度值为 {}", configType, sensitivityValue);
        
        // 验证灵敏度值不能为null
        if (sensitivityValue == null) {
            log.error("灵敏度值不能为null");
            return false;
        }

        int result = sensitivityConfigMapper.updateSensitivityByConfigType(configType, sensitivityValue);
        return result > 0;
    }

    @Override
    public Double getPeakHourSensitivity() {
        String configType = "peak_hour_sensitivity";
        SensitivityConfig config = getSensitivityByConfigType(configType);
        
        if (config != null && config.getSensitivityValue() != null) {
            return config.getSensitivityValue();
        }
        
        // 如果没有配置，则返回默认值
        return 0.15;
    }

    @Override
    public boolean updatePeakHourSensitivity(Double sensitivityValue) {
        String configType = "peak_hour_sensitivity";
        
        // 验证高峰时段灵敏度值是否在有效范围内（0.0 - 1.0）
        if (sensitivityValue == null || sensitivityValue < 0.0 || sensitivityValue > 1.0) {
            log.error("高峰时段灵敏度值 {} 不在有效范围内 (0.0 - 1.0)", sensitivityValue);
            return false;
        }
        
        return updateSensitivityByConfigType(configType, sensitivityValue);
    }
}