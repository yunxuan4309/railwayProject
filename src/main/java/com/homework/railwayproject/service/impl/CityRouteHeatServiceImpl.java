package com.homework.railwayproject.service.impl;

import com.homework.railwayproject.mapper.CityRouteHeatMapper;
import com.homework.railwayproject.pojo.entity.CityRouteHeat;
import com.homework.railwayproject.service.CityRouteHeatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 城市路线热度服务实现类
 */
@Slf4j
@Service
public class CityRouteHeatServiceImpl implements CityRouteHeatService {
    
    @Autowired
    private CityRouteHeatMapper cityRouteHeatMapper;
    
    @Override
    public List<CityRouteHeat> getCityRouteHeatByDate(LocalDate date) {
        return cityRouteHeatMapper.selectCityRouteHeatByDate(date);
    }
}