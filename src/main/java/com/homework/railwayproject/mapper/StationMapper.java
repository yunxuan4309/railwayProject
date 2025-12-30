package com.homework.railwayproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homework.railwayproject.pojo.entity.Station;
import org.apache.ibatis.annotations.Mapper;

/**
 * 站点信息Mapper接口
 */
@Mapper
public interface StationMapper extends BaseMapper<Station> {

    /**
     * 统计所有站点数量
     *
     * @return 站点总数
     */
    Long countAllStations();
}