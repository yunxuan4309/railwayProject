package com.homework.railwayproject.service.impl;

import com.homework.railwayproject.mapper.HighSpeedPassengerCleanMapper;
import com.homework.railwayproject.mapper.LineMapper;
import com.homework.railwayproject.mapper.StationMapper;
import com.homework.railwayproject.pojo.dto.StatisticsResult;
import com.homework.railwayproject.pojo.entity.Line;
import com.homework.railwayproject.pojo.entity.Station;
import com.homework.railwayproject.service.DataSumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据统计服务实现类
 */
@Slf4j
@Service
public class DataSumServiceImpl implements DataSumService {

    @Autowired
    private HighSpeedPassengerCleanMapper highSpeedPassengerCleanMapper;

    @Autowired
    private LineMapper lineMapper;

    @Autowired
    private StationMapper stationMapper;

    @Override
    public StatisticsResult countCleanData() {
        log.info("开始统计清洗表数据量");
        Long count = highSpeedPassengerCleanMapper.countAllCleanData();
        log.info("清洗表数据量统计完成，总数: {}", count);
        return new StatisticsResult("clean_data_count", count, "清洗表中数据总条数");
    }

    @Override
    public StatisticsResult countLines() {
        log.info("开始统计线路数量");
        Long count = lineMapper.countAllLines();
        log.info("线路数量统计完成，总数: {}", count);
        return new StatisticsResult("line_count", count, "线路总数量");
    }

    @Override
    public StatisticsResult countStations() {
        log.info("开始统计站点数量");
        Long count = stationMapper.countAllStations();
        log.info("站点数量统计完成，总数: {}", count);
        return new StatisticsResult("station_count", count, "站点总数量");
    }
}