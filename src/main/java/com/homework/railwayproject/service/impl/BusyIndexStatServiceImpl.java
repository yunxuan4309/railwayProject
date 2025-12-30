package com.homework.railwayproject.service.impl;

import com.homework.railwayproject.mapper.BusyIndexStatMapper;
import com.homework.railwayproject.pojo.entity.BusyIndexStat;
import com.homework.railwayproject.service.BusyIndexStatService;
import com.homework.railwayproject.service.SensitivityConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    public List<BusyIndexStat> getTop20BusyIndexStations(LocalDate startTime, LocalDate endTime) {

        // 验证参数
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("开始日期和结束日期不能为空");
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }
        // 生成缓存key
        String cacheKey = "Busy-top20" + startTime + ":" + endTime;

        // 尝试从缓存获取
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        List<BusyIndexStat> cachedList = (List<BusyIndexStat>) ops.get(cacheKey);
        if (cachedList != null) {
            log.debug("从缓存获取站点客流统计: {}", cacheKey);
            return cachedList;
        }
        Double defaultSensitivity = sensitivityConfigService.getPeakHourSensitivity();
        // 缓存未命中，查询数据库
        List<BusyIndexStat> list = busyIndexStatMapper.selectTop20BusyIndexStations(startTime, endTime, defaultSensitivity);

        // 存入缓存，有效期2小时
        ops.set(cacheKey, list, 2, java.util.concurrent.TimeUnit.HOURS);

        return list;
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