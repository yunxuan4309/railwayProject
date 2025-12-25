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

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 缓存键名
     */
    private static final String BUSY_INDEX_TOP20_KEY = "station:busyindex:top20";

    @Override
    public List<BusyIndexStat> getTop20BusyIndexStations() {
        // 先尝试从缓存获取
        List<BusyIndexStat> cachedList = null;
        try {
            cachedList = (List<BusyIndexStat>) redisTemplate.opsForValue().get(BUSY_INDEX_TOP20_KEY);
        } catch (SerializationException e) {
            log.warn("Redis反序列化失败，将清除缓存并重新加载数据", e);
            // 清除缓存中的无效数据
            redisTemplate.delete(BUSY_INDEX_TOP20_KEY);
            cachedList = null;
        }

        if (cachedList != null && !cachedList.isEmpty()) {
            return cachedList;
        }

        // 缓存未命中或反序列化失败，则查询数据库
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMinutes(10); // 查询最近10分钟的数据

        List<BusyIndexStat> list = busyIndexStatMapper.selectTop20BusyIndexStations(startTime, endTime);

        // 将结果存入缓存，过期时间设置为10分钟
        redisTemplate.opsForValue().set(BUSY_INDEX_TOP20_KEY, list, 600);

        return list;
    }

    @Override
    public void calculateAndCacheTop20BusyIndexStations() {
        log.info("开始统计站点繁忙指数前20名");

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMinutes(10); // 查询最近10分钟的数据

        List<BusyIndexStat> list = busyIndexStatMapper.selectTop20BusyIndexStations(startTime, endTime);

        // 将结果存入缓存，过期时间设置为10分钟
        try {
            redisTemplate.opsForValue().set(BUSY_INDEX_TOP20_KEY, list, 600);
        } catch (SerializationException e) {
            log.error("Redis序列化失败，无法缓存数据", e);
        }

        log.info("站点繁忙指数前20名统计完成，共{}条记录", list.size());
    }

    @Override
    public BusyIndexStat getBusyIndexStatByIdAndTime(Integer siteId, LocalDate startTime, LocalDate endTime) {
        return busyIndexStatMapper.selectBusyIndexStatByIdAndTime(siteId, startTime, endTime);
    }
}