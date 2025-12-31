package com.homework.railwayproject.mapper;

import com.homework.railwayproject.pojo.entity.SensitivityConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 站点等级容量配置Mapper接口
 * 通过SensitivityConfig表管理站点等级容量配置
 */
@Mapper
public interface StationLevelCapacityMapper {

    /**
     * 根据站点等级获取容量配置
     *
     * @param stationLevel 站点等级
     * @return 灵敏度配置（包含容量信息）
     */
    SensitivityConfig selectByStationLevel(@Param("stationLevel") String stationLevel);
    
    /**
     * 获取所有启用的容量配置
     *
     * @return 灵敏度配置列表
     */
    java.util.List<SensitivityConfig> selectAllEnabled();
}