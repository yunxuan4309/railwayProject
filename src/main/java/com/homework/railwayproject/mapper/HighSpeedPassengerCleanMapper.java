package com.homework.railwayproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homework.railwayproject.pojo.entity.HighSpeedPassengerClean;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 高铁乘客清洗数据Mapper接口
 */
@Mapper
public interface HighSpeedPassengerCleanMapper extends BaseMapper<HighSpeedPassengerClean> {
    int insertBatchSomeColumn(List<HighSpeedPassengerClean> passengers);
}