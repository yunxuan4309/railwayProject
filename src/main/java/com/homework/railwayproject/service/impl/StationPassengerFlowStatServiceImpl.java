package com.homework.railwayproject.service.impl;

import com.homework.railwayproject.mapper.StationPassengerFlowStatMapper;
import com.homework.railwayproject.pojo.entity.StationPassengerFlowStat;
import com.homework.railwayproject.service.StationPassengerFlowStatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 站点客流统计服务实现类
 */
@Slf4j
@Service
public class StationPassengerFlowStatServiceImpl implements StationPassengerFlowStatService {
    
    @Autowired
    private StationPassengerFlowStatMapper stationPassengerFlowStatMapper;

    
    @Override
    public List<StationPassengerFlowStat> getTop20Stations() {

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(1);
        
        List<StationPassengerFlowStat> list = stationPassengerFlowStatMapper.selectTop20Stations(startDate, endDate);
        


        return list;
    }
    
    @Override
    public void calculateAndCacheTop20Stations() {
        log.info("开始统计站点客流前20名");
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(1);
        
        List<StationPassengerFlowStat> list = stationPassengerFlowStatMapper.selectTop20Stations(startDate, endDate);

    }

    @Override
    public StationPassengerFlowStat getStationPassengerFlow(Integer siteId) {
        return stationPassengerFlowStatMapper.getStationPassengerFlow(siteId);
    }
}