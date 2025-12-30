package com.homework.railwayproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homework.railwayproject.pojo.entity.HighSpeedPassenger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 高铁乘客数据Mapper接口
 */
@Mapper
public interface HighSpeedPassengerMapper extends BaseMapper<HighSpeedPassenger> {
    int insertBatchSomeColumn(List<HighSpeedPassenger> passengers);
    
    int updateStatusInBatch(@Param("ids") List<Long> ids, @Param("newStatus") Integer newStatus);
}
