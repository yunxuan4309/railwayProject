package com.homework.railwayproject.service.impl;

import com.homework.railwayproject.mapper.StationPassengerFlowStatMapper;
import com.homework.railwayproject.pojo.entity.StationPassengerFlowStat;
import com.homework.railwayproject.service.StationPassengerFlowStatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class StationPassengerFlowStatServiceImpl implements StationPassengerFlowStatService {
    
    @Autowired
    private StationPassengerFlowStatMapper stationPassengerFlowStatMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    
    @Override
    public List<StationPassengerFlowStat> getTop20Stations() {

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(1);
        
        List<StationPassengerFlowStat> list = stationPassengerFlowStatMapper.selectTop20Stations(startDate, endDate);
        


        return list;
    }
    
    @Override
    public List<StationPassengerFlowStat> getTop20StationsByDate(LocalDate startDate, LocalDate endDate) {
        // 验证参数
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("开始日期和结束日期不能为空");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }
        
        // 生成缓存key
        String cacheKey = "station:top20:" + startDate + ":" + endDate;
        
        // 尝试从缓存获取
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        List<StationPassengerFlowStat> cachedList = (List<StationPassengerFlowStat>) ops.get(cacheKey);
        if (cachedList != null) {
            log.debug("从缓存获取站点客流统计: {}", cacheKey);
            return cachedList;
        }
        
        // 缓存未命中，查询数据库
        List<StationPassengerFlowStat> list = stationPassengerFlowStatMapper.selectTop20Stations(startDate, endDate);
        
        // 存入缓存，有效期2小时
        ops.set(cacheKey, list, 2, java.util.concurrent.TimeUnit.HOURS);
        
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
        if (siteId == null) {
            throw new IllegalArgumentException("站点ID不能为空");
        }
        return stationPassengerFlowStatMapper.getStationPassengerFlow(siteId);
    }

    @Override
    public StationPassengerFlowStat getStationPassengerFlowByDate(Integer siteId, LocalDate startDate, LocalDate endDate) {
        // 验证参数
        if (siteId == null) {
            throw new IllegalArgumentException("站点ID不能为空");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("开始日期和结束日期不能为空");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }
        
        return stationPassengerFlowStatMapper.getStationPassengerFlowByDate(siteId, startDate, endDate);
    }
}