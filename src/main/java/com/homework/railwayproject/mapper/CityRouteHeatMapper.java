package com.homework.railwayproject.mapper;

import com.homework.railwayproject.pojo.entity.CityRouteHeat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 城市路线热度Mapper接口
 */
@Mapper
public interface CityRouteHeatMapper {
    
    /**
     * 查询指定日期的城市间客流热度排行
     * 
     * @param date 指定日期
     * @return 城市路线热度列表
     */
    List<CityRouteHeat> selectCityRouteHeatByDate(@Param("date") LocalDate date);
}