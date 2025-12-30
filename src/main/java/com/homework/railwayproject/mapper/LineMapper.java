package com.homework.railwayproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homework.railwayproject.pojo.entity.Line;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 线路信息Mapper接口
 */
@Mapper
public interface LineMapper extends BaseMapper<Line> {

    /**
     * 查询所有线路信息
     *
     * @return 线路列表
     */
    List<Line> selectAllLines();

    /**
     * 统计线路总数
     *
     * @return 线路总数量
     */
    Long countAllLines();
}