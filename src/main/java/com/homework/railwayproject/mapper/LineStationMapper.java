package com.homework.railwayproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homework.railwayproject.pojo.entity.LineStation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LineStationMapper extends BaseMapper<LineStation> {

    /**
     * 按线路编码和顺序查询站点
     */
    List<LineStation> selectByLineCodeOrdered(@Param("lineCode") String lineCode);

    /**
     * 查询所有线路的站点（按线路和顺序排序）
     */
    List<LineStation> selectAllWithOrder();
}