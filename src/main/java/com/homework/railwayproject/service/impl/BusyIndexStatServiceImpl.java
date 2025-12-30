package com.homework.railwayproject.service.impl;

import com.homework.railwayproject.mapper.BusyIndexStatMapper;
import com.homework.railwayproject.pojo.entity.BusyIndexStat;
import com.homework.railwayproject.service.BusyIndexStatService;
import com.homework.railwayproject.service.SensitivityConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 繁忙指数统计服务实现类
 */
@Slf4j
@Service
public class BusyIndexStatServiceImpl implements BusyIndexStatService {

    @Autowired
    private BusyIndexStatMapper busyIndexStatMapper;
    @Autowired
    private SensitivityConfigService sensitivityConfigService;
    @Override
    public List<BusyIndexStat> getTop20BusyIndexStations(LocalDate startTime, LocalDate endTime) {

        Double defaultSensitivity = sensitivityConfigService.getPeakHourSensitivity();


        return busyIndexStatMapper.selectTop20BusyIndexStations(startTime, endTime, defaultSensitivity);
    }

    @Override
    public void calculateAndCacheTop20BusyIndexStations() {
        log.info("开始统计站点繁忙指数前20名");

        LocalDate endTime = LocalDate.now();
        LocalDate startTime = endTime.minusDays(1);
        Double defaultSensitivity = sensitivityConfigService.getPeakHourSensitivity();
        List<BusyIndexStat> list = busyIndexStatMapper.selectTop20BusyIndexStations(startTime, endTime, defaultSensitivity);

    }

    @Override
    public BusyIndexStat getBusyIndexStatByIdAndTime(Integer siteId, LocalDate startTime, LocalDate endTime) {
        Double defaultSensitivity = sensitivityConfigService.getPeakHourSensitivity();
        return busyIndexStatMapper.selectBusyIndexStatByIdAndTime(siteId, startTime, endTime, defaultSensitivity);
    }
}