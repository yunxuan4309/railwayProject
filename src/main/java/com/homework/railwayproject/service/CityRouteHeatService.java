package com.homework.railwayproject.service;

import com.homework.railwayproject.pojo.entity.CityRouteHeat;

import java.time.LocalDate;
import java.util.List;

/**
 * 城市路线热度服务接口
 */
public interface CityRouteHeatService {
    
    /**
     * 获取指定日期的城市路线热度排行
     * 
     * @param date 指定日期
     * @return 城市路线热度列表
     */
    List<CityRouteHeat> getCityRouteHeatByDate(LocalDate date);
}