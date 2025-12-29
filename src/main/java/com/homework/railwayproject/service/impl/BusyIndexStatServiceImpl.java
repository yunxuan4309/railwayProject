package com.homework.railwayproject.service.impl;

import com.homework.railwayproject.mapper.BusyIndexStatMapper;
import com.homework.railwayproject.pojo.entity.BusyIndexStat;
import com.homework.railwayproject.service.BusyIndexStatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 繁忙指数统计服务实现类
 */
@Slf4j
@Service
public class BusyIndexStatServiceImpl implements BusyIndexStatService {

    @Autowired
    private BusyIndexStatMapper busyIndexStatMapper;

    @Override
    public List<BusyIndexStat> getTop20BusyIndexStations() {

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMinutes(10); // 查询最近10分钟的数据

        List<BusyIndexStat> list = busyIndexStatMapper.selectTop20BusyIndexStations(startTime, endTime);


        return list;
    }

    @Override
    public void calculateAndCacheTop20BusyIndexStations() {
        log.info("开始统计站点繁忙指数前20名");

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMinutes(10); // 查询最近10分钟的数据

        List<BusyIndexStat> list = busyIndexStatMapper.selectTop20BusyIndexStations(startTime, endTime);

    }

    @Override
    public BusyIndexStat getBusyIndexStatByIdAndTime(Integer siteId, LocalDate startTime, LocalDate endTime) {
        return busyIndexStatMapper.selectBusyIndexStatByIdAndTime(siteId, startTime, endTime);
    }
}