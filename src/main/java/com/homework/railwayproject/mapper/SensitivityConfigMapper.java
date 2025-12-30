package com.homework.railwayproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homework.railwayproject.pojo.entity.SensitivityConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 灵敏度配置Mapper接口
 */
@Mapper
public interface SensitivityConfigMapper extends BaseMapper<SensitivityConfig> {

    /**
     * 根据配置类型获取灵敏度配置
     *
     * @param configType 配置类型
     * @return 灵敏度配置
     */
    SensitivityConfig selectByConfigType(@Param("configType") String configType);

    /**
     * 根据配置类型更新灵敏度值
     *
     * @param configType 配置类型
     * @param sensitivityValue 灵敏度值
     * @return 更新记录数
     */
    int updateSensitivityByConfigType(@Param("configType") String configType,
                                      @Param("sensitivityValue") Double sensitivityValue);
}