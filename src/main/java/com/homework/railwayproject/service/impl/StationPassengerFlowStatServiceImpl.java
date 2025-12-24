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
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    /**
     * 缓存键名
     */
    private static final String STATION_PASSENGER_FLOW_TOP20_KEY = "station:passengerflow:top20";
    
    @Override
    public List<StationPassengerFlowStat> getTop20Stations() {
        // 先尝试从缓存获取
        List<StationPassengerFlowStat> cachedList = null;
        try {
            cachedList = (List<StationPassengerFlowStat>) redisTemplate.opsForValue().get(STATION_PASSENGER_FLOW_TOP20_KEY);
        } catch (SerializationException e) {
            log.warn("Redis反序列化失败，将清除缓存并重新加载数据", e);
            // 清除缓存中的无效数据
            redisTemplate.delete(STATION_PASSENGER_FLOW_TOP20_KEY);
            cachedList = null;
        }
        
        if (cachedList != null && !cachedList.isEmpty()) {
            return cachedList;
        }
        
        // 缓存未命中或反序列化失败，则查询数据库
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(1);
        
        List<StationPassengerFlowStat> list = stationPassengerFlowStatMapper.selectTop20Stations(startDate, endDate);
        
        // 将结果存入缓存，过期时间设置为1小时
        redisTemplate.opsForValue().set(STATION_PASSENGER_FLOW_TOP20_KEY, list, 3600);
        
        return list;
    }
    
    @Override
    public void calculateAndCacheTop20Stations() {
        log.info("开始统计站点客流前20名");
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(1);
        
        List<StationPassengerFlowStat> list = stationPassengerFlowStatMapper.selectTop20Stations(startDate, endDate);
        
        // 将结果存入缓存，过期时间设置为24小时
        try {
            redisTemplate.opsForValue().set(STATION_PASSENGER_FLOW_TOP20_KEY, list, 86400);
        } catch (SerializationException e) {
            log.error("Redis序列化失败，无法缓存数据", e);
        }
        
        log.info("站点客流前20名统计完成，共{}条记录", list.size());
    }

    @Override
    public StationPassengerFlowStat getStationPassengerFlow(Integer siteId) {
        return stationPassengerFlowStatMapper.getStationPassengerFlow(siteId);
    }
}