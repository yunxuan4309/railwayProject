package com.homework.railwayproject.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homework.railwayproject.pojo.dto.StationLevelStatDTO;
import com.homework.railwayproject.pojo.dto.StationLevelValidateDTO;
import com.homework.railwayproject.pojo.dto.StationLevelValidateResultDTO;
import com.homework.railwayproject.pojo.entity.Station;

import java.util.List;

public interface StationService extends IService<Station> {

    IPage<Station> getStationPage(Page<Station> page, String stationName, String city);

    Station getStationById(Integer siteId);

    Station addStation(Station station);

    Station updateStation(Station station);

    Boolean deleteStation(Integer siteId);

    StationLevelStatDTO getStationLevelStat();

    StationLevelValidateResultDTO validateStationLevel(StationLevelValidateDTO validateDTO);

    List<Station> searchStationsByName(String stationName);
}