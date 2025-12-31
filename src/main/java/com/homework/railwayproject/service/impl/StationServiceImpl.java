package com.homework.railwayproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homework.railwayproject.mapper.StationMapper;
import com.homework.railwayproject.pojo.dto.StationLevelStatDTO;
import com.homework.railwayproject.pojo.dto.StationLevelValidateDTO;
import com.homework.railwayproject.pojo.dto.StationLevelValidateResultDTO;
import com.homework.railwayproject.pojo.entity.Station;
import com.homework.railwayproject.service.StationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements StationService {

    private static final List<String> VALID_LEVELS = Arrays.asList("特级", "一级", "二级", "三级", "四级", "五级");

    @Override
    public IPage<Station> getStationPage(Page<Station> page, String stationName, String city) {
        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Station::getIsDeleted, 0);

        if (StringUtils.hasText(stationName)) {
            wrapper.like(Station::getStationName, stationName);
        }

        if (StringUtils.hasText(city)) {
            wrapper.like(Station::getCity, city);
        }

        wrapper.orderByDesc(Station::getCreateTime);

        return page(page, wrapper);
    }

    @Override
    public Station getStationById(Integer siteId) {
        if (siteId == null) {
            return null;
        }
        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Station::getSiteId, siteId);
        wrapper.eq(Station::getIsDeleted, 0);
        return getOne(wrapper);
    }

    @Override
    public Station addStation(Station station) {
        if (station == null) {
            return null;
        }
        station.setIsDeleted(0);
        save(station);
        return station;
    }

    @Override
    public Station updateStation(Station station) {
        if (station == null || station.getSiteId() == null) {
            return null;
        }
        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Station::getSiteId, station.getSiteId());
        wrapper.eq(Station::getIsDeleted, 0);

        Station existingStation = getOne(wrapper);
        if (existingStation == null) {
            return null;
        }

        update(station, wrapper);
        return station;
    }

    @Override
    public Boolean deleteStation(Integer siteId) {
        if (siteId == null) {
            return false;
        }
        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Station::getSiteId, siteId);
        wrapper.eq(Station::getIsDeleted, 0);

        Station station = getOne(wrapper);
        if (station == null) {
            return false;
        }

        station.setIsDeleted(1);
        return update(station, wrapper);
    }

    @Override
    public StationLevelStatDTO getStationLevelStat() {
        StationLevelStatDTO statDTO = new StationLevelStatDTO();
        Long totalStations = baseMapper.countAllStations();
        statDTO.setTotalStations(totalStations);

        List<Map<String, Object>> levelList = baseMapper.countStationsByLevel();
        Map<String, Long> levelDistribution = new java.util.HashMap<>();
        for (Map<String, Object> levelMap : levelList) {
            String level = (String) levelMap.get("level");
            Long count = ((Number) levelMap.get("count")).longValue();
            levelDistribution.put(level, count);
        }
        statDTO.setLevelDistribution(levelDistribution);

        return statDTO;
    }

    @Override
    public StationLevelValidateResultDTO validateStationLevel(StationLevelValidateDTO validateDTO) {
        StationLevelValidateResultDTO result = new StationLevelValidateResultDTO();
        result.setSiteId(validateDTO.getSiteId());
        result.setStationLevel(validateDTO.getStationLevel());

        if (validateDTO.getSiteId() == null) {
            result.setIsValid(false);
            result.setMessage("站点ID不能为空");
            return result;
        }

        Station station = getStationById(validateDTO.getSiteId());
        if (station == null) {
            result.setIsValid(false);
            result.setMessage("站点不存在");
            return result;
        }

        result.setStationName(station.getStationName());

        if (!StringUtils.hasText(validateDTO.getStationLevel())) {
            result.setIsValid(false);
            result.setMessage("站点等级不能为空");
            return result;
        }

        String level = validateDTO.getStationLevel().trim();
        if (VALID_LEVELS.contains(level)) {
            result.setIsValid(true);
            result.setMessage("站点等级格式正确");
        } else {
            result.setIsValid(false);
            result.setMessage("站点等级格式错误，有效值为：特级、一级、二级、三级、四级、五级");
        }

        return result;
    }
}
