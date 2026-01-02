package com.homework.railwayproject.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homework.railwayproject.mapper.*;
import com.homework.railwayproject.pojo.dto.SectionLoadRateQueryDTO;
import com.homework.railwayproject.pojo.entity.SectionHourlyFlow;
import com.homework.railwayproject.pojo.entity.SectionDailyFlow;
import com.homework.railwayproject.pojo.entity.OverloadAlert;
import com.homework.railwayproject.pojo.entity.Station;
import com.homework.railwayproject.pojo.vo.*;
import com.homework.railwayproject.service.LineOptimizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
public class LineOptimizationServiceImpl extends ServiceImpl<LineOptimizationMapper, SectionHourlyFlow>
        implements LineOptimizationService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private LineOptimizationMapper lineOptimizationMapper;

    @Autowired
    private StationMapper stationMapper;

    @Override
    public List<LoadRateVO> calculateSectionLoadRate(SectionLoadRateQueryDTO query) {
        // 使用新的缓存键策略，包含更多参数
        String cacheKey = "load:rate:hourly:" + (query.getLineCode() != null ? query.getLineCode() : "all")
                + ":" + query.getFlowDate()
                + ":" + (query.getStartHour() != null ? query.getStartHour() : "0")
                + ":" + (query.getEndHour() != null ? query.getEndHour() : "23")
                + ":startStationId" + (query.getStartStationId() != null ? query.getStartStationId() : "all")
                + ":endStationId" + (query.getEndStationId() != null ? query.getEndStationId() : "all")
                + ":startStationName" + (query.getStartStationName() != null ? query.getStartStationName() : "all")
                + ":endStationName" + (query.getEndStationName() != null ? query.getEndStationName() : "all");

        List<LoadRateVO> cachedResult = (List<LoadRateVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            log.info("从缓存获取区间满载率数据，key: {}", cacheKey);
            return cachedResult;
        }

        // 使用新的直接查询方法
        List<LoadRateVO> result = lineOptimizationMapper.selectSectionLoadRateFromCleanData(query);

        // 为每个结果设置timeRange，因为SQL中的CONCAT不能在GROUP BY中使用
        for (LoadRateVO loadRateVO : result) {
            if (loadRateVO.getHour() != null) {
                String hourStr = String.format("%02d", loadRateVO.getHour());
                loadRateVO.setTimeRange(hourStr + ":00-" + hourStr + ":59");
            }
        }

        // 设置缓存，2小时过期
        redisTemplate.opsForValue().set(cacheKey, result, 2, TimeUnit.HOURS);
        log.info("区间满载率数据已缓存，key: {}, 数据条数: {}", cacheKey, result.size());

        return result;
    }

    @Override
    public List<LoadRateVO> calculateSectionLoadRateFromCleanData(SectionLoadRateQueryDTO query) {
        return calculateSectionLoadRate(query);
    }

    // 新增分页查询方法
    public List<LoadRateVO> calculateSectionLoadRateWithPaging(SectionLoadRateQueryDTO query) {
        // 使用新的缓存键策略，包含分页参数
        String cacheKey = "load:rate:hourly:paging:" + (query.getLineCode() != null ? query.getLineCode() : "all")
                + ":" + query.getFlowDate()
                + ":" + (query.getStartHour() != null ? query.getStartHour() : "0")
                + ":" + (query.getEndHour() != null ? query.getEndHour() : "23")
                + ":startStationId" + (query.getStartStationId() != null ? query.getStartStationId() : "all")
                + ":endStationId" + (query.getEndStationId() != null ? query.getEndStationId() : "all")
                + ":startStationName" + (query.getStartStationName() != null ? query.getStartStationName() : "all")
                + ":endStationName" + (query.getEndStationName() != null ? query.getEndStationName() : "all")
                + ":page" + query.getPage() + ":size" + query.getSize();

        List<LoadRateVO> cachedResult = (List<LoadRateVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            log.info("从缓存获取区间满载率分页数据，key: {}", cacheKey);
            return cachedResult;
        }

        // 计算OFFSET
        int offset = (query.getPage() - 1) * query.getSize();

        // 为分页查询添加offset参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("query", query);
        paramMap.put("offset", offset);
        
        // 使用新的分页查询方法
        List<LoadRateVO> result = lineOptimizationMapper.selectSectionLoadRateFromCleanDataWithPaging(paramMap);

        // 为每个结果设置timeRange
        for (LoadRateVO loadRateVO : result) {
            if (loadRateVO.getHour() != null) {
                String hourStr = String.format("%02d", loadRateVO.getHour());
                loadRateVO.setTimeRange(hourStr + ":00-" + hourStr + ":59");
            }
        }

        // 设置缓存，2小时过期
        redisTemplate.opsForValue().set(cacheKey, result, 2, TimeUnit.HOURS);
        log.info("区间满载率分页数据已缓存，key: {}, 数据条数: {}", cacheKey, result.size());

        return result;
    }

    @Override
    public Integer getSectionLoadRateCount(SectionLoadRateQueryDTO query) {
        return lineOptimizationMapper.selectSectionLoadRateCount(query);
    }
    
    /**
     * 获取分页的区间满载率数据
     */
    public IPage<LoadRateVO> getSectionLoadRateWithPaging(SectionLoadRateQueryDTO query) {
        // 创建分页对象
        Page<LoadRateVO> page = new Page<>(query.getPage(), query.getSize());
        
        // 使用新的缓存键策略，包含分页参数
        String cacheKey = "load:rate:hourly:paging:iPage:" + (query.getLineCode() != null ? query.getLineCode() : "all")
                + ":" + query.getFlowDate()
                + ":" + (query.getStartHour() != null ? query.getStartHour() : "0")
                + ":" + (query.getEndHour() != null ? query.getEndHour() : "23")
                + ":startStationId" + (query.getStartStationId() != null ? query.getStartStationId() : "all")
                + ":endStationId" + (query.getEndStationId() != null ? query.getEndStationId() : "all")
                + ":startStationName" + (query.getStartStationName() != null ? query.getStartStationName() : "all")
                + ":endStationName" + (query.getEndStationName() != null ? query.getEndStationName() : "all")
                + ":page" + query.getPage() + ":size" + query.getSize();

        // 尝试从缓存获取完整分页结果
        String countCacheKey = cacheKey + ":count";
        
        IPage<LoadRateVO> cachedResult = (IPage<LoadRateVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            log.info("从缓存获取区间满载率分页数据，key: {}", cacheKey);
            return cachedResult;
        }

        // 计算OFFSET
        int offset = (query.getPage() - 1) * query.getSize();

        // 为分页查询添加offset参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("query", query);
        paramMap.put("offset", offset);
        
        // 查询分页数据
        List<LoadRateVO> result = lineOptimizationMapper.selectSectionLoadRateFromCleanDataWithPaging(paramMap);

        // 为每个结果设置timeRange
        for (LoadRateVO loadRateVO : result) {
            if (loadRateVO.getHour() != null) {
                String hourStr = String.format("%02d", loadRateVO.getHour());
                loadRateVO.setTimeRange(hourStr + ":00-" + hourStr + ":59");
            }
        }

        // 查询总数
        Integer total = lineOptimizationMapper.selectSectionLoadRateCount(query);
        
        // 构建分页结果
        IPage<LoadRateVO> pageResult = new Page<>();
        pageResult.setRecords(result);
        pageResult.setTotal(total);
        pageResult.setSize(query.getSize());
        pageResult.setCurrent(query.getPage());
        pageResult.setPages(total != null ? (int) Math.ceil((double) total / query.getSize()) : 0);

        // 设置缓存，2小时过期
        redisTemplate.opsForValue().set(cacheKey, pageResult, 2, TimeUnit.HOURS);
        log.info("区间满载率分页数据已缓存，key: {}, 总数: {}, 当前页数据条数: {}", cacheKey, total, result.size());

        return pageResult;
    }

    @Override
    public List<OverloadAlertVO> getOverloadAlerts() {
        List<OverloadAlertVO> alerts = lineOptimizationMapper.selectActiveOverloadAlerts();

        for (OverloadAlertVO alert : alerts) {
            setSectionNameForAlert(alert);
        }

        return alerts;
    }

    private void setSectionNameForAlert(OverloadAlertVO alert) {
        Station startStation = stationMapper.selectById(alert.getStartStationId());
        Station endStation = stationMapper.selectById(alert.getEndStationId());

        String startName = startStation != null ? startStation.getStationName() : "未知";
        String endName = endStation != null ? endStation.getStationName() : "未知";

        alert.setStartStationName(startName);
        alert.setEndStationName(endName);
        alert.setSection(startName + "-" + endName);
    }

    @Override
    public List<TrainAdditionSuggestionVO> generateAdditionSuggestions() {
        List<OverloadAlertVO> alerts = getOverloadAlerts();
        List<TrainAdditionSuggestionVO> suggestions = new ArrayList<>();

        for (OverloadAlertVO alert : alerts) {
            // 使用原有的方法从section_hourly_flow表计算高峰时段
            List<Map<String, Object>> peakHours = lineOptimizationMapper
                    .selectPeakHoursBySection(
                            alert.getLineCode(),
                            alert.getStartStationId(),
                            alert.getEndStationId(),
                            alert.getAlertStartDate(),
                            alert.getAlertEndDate());

            for (Map<String, Object> peakHour : peakHours) {
                Integer hour = (Integer) peakHour.get("hour");
                Double loadRate = (Double) peakHour.get("avg_load_rate");

                if (loadRate != null && loadRate > 80) { // 恢复原来的阈值
                    TrainAdditionSuggestionVO suggestion = new TrainAdditionSuggestionVO();
                    suggestion.setLineCode(alert.getLineCode());
                    suggestion.setSection(alert.getSection());
                    suggestion.setSuggestedTrainNumber(generateTrainNumber(alert.getLineCode()));
                    suggestion.setDepartureTime(LocalTime.of(hour, 0));
                    suggestion.setArrivalTime(LocalTime.of(hour, 30));
                    suggestion.setCarriageCount(8);
                    suggestion.setTrainType("CR400AF");
                    suggestion.setReason(String.format("该区间在%d:00时段满载率高达%.2f%%，建议加开列车", hour, loadRate));
                    suggestion.setExpectedLoadRate(loadRate * 0.7);

                    suggestions.add(suggestion);
                }
            }
        }

        return suggestions;
    }

    private String generateTrainNumber(String lineCode) {
        String prefix = "G";
        Random random = new Random();
        int number = 1000 + random.nextInt(9000);
        return prefix + number;
    }

    @Override
    public void calculateAndSaveSectionStatistics(LocalDate flowDate) {
        log.info("开始计算{}的区间客流统计", flowDate);

        try {
            // 1. 计算每小时统计数据
            for (int hour = 0; hour < 24; hour++) {
                calculateHourlyStatistics(flowDate, hour);
            }

            // 2. 计算每日统计数据
            calculateDailyStatistics(flowDate);

            // 3. 检测连续过载区间
            detectContinuousOverloadSections(flowDate);

            log.info("完成计算{}的区间客流统计", flowDate);
        } catch (Exception e) {
            log.error("计算区间客流统计失败", e);
            throw e;
        }
    }

    private void calculateHourlyStatistics(LocalDate flowDate, int hour) {
        // 计算每个区间每小时的客流量和满载率
        List<SectionHourlyFlow> hourlyFlows = lineOptimizationMapper
                .calculateSectionHourlyFlow(flowDate, hour);

        // 批量保存或更新
        for (SectionHourlyFlow flow : hourlyFlows) {
            // 计算满载率，如果trainCapacity大于0，则计算loadRate，否则设置为0
            if (flow.getTrainCapacity() != null && flow.getTrainCapacity() > 0) {
                double loadRate = ((double) flow.getPassengerCount() / flow.getTrainCapacity()) * 100.0;
                flow.setLoadRate(loadRate);
            } else {
                flow.setLoadRate(0.0);
            }

            // 使用 LambdaQueryWrapper 查询是否已存在
            SectionHourlyFlow existing = this.lambdaQuery()
                    .eq(SectionHourlyFlow::getLineCode, flow.getLineCode())
                    .eq(SectionHourlyFlow::getStartStationId, flow.getStartStationId())
                    .eq(SectionHourlyFlow::getEndStationId, flow.getEndStationId())
                    .eq(SectionHourlyFlow::getFlowDate, flowDate)
                    .eq(SectionHourlyFlow::getHour, hour)
                    .eq(SectionHourlyFlow::getIsDeleted, 0)
                    .one();

            if (existing != null) {
                flow.setId(existing.getId());  // 现在有 getId() 方法了
                this.updateById(flow);
            } else {
                this.save(flow);
            }
        }
    }

    private void calculateDailyStatistics(LocalDate flowDate) {
        // 从小时表汇总生成日统计数据
        List<SectionDailyFlow> dailyFlows = lineOptimizationMapper
                .calculateSectionDailyFlow(flowDate);

        // 批量保存或更新
        for (SectionDailyFlow flow : dailyFlows) {
            SectionDailyFlow existing = lineOptimizationMapper.selectDailyFlow(
                    flow.getLineCode(),
                    flow.getStartStationId(),
                    flow.getEndStationId(),
                    flowDate);

            if (existing != null) {
                flow.setId(existing.getId());  // 现在有 getId() 方法了
                lineOptimizationMapper.updateDailyFlow(flow);
            } else {
                lineOptimizationMapper.insertDailyFlow(flow);
            }
        }
    }

    @Override
    public void detectContinuousOverloadSections(LocalDate checkDate) {
        // 查询连续7天超过90%的区间
        LocalDate startDate = checkDate.minusDays(6);

        List<OverloadAlertVO> overloadSections = lineOptimizationMapper
                .selectContinuousOverloadSections(startDate, checkDate, 7, 90.0);

        // 保存告警记录
        for (OverloadAlertVO alert : overloadSections) {
            // 检查是否已有相同的告警 - 使用实体类而不是VO
            OverloadAlertVO existing = lineOptimizationMapper.selectOverloadAlert(
                    alert.getLineCode(),
                    alert.getStartStationId(),
                    alert.getEndStationId(),
                    alert.getAlertStartDate(),
                    alert.getAlertEndDate());

            if (existing == null) {
                // 插入新告警
                OverloadAlert newAlert = convertToEntity(alert);
                lineOptimizationMapper.insertOverloadAlert(newAlert);
            }
        }

        log.info("检测到{}个连续过载区间", overloadSections.size());
    }

    private OverloadAlert convertToEntity(OverloadAlertVO vo) {
        OverloadAlert alert = new OverloadAlert();
        alert.setLineCode(vo.getLineCode());
        alert.setStartStationId(vo.getStartStationId());
        alert.setEndStationId(vo.getEndStationId());
        alert.setAlertStartDate(vo.getAlertStartDate());
        alert.setAlertEndDate(vo.getAlertEndDate());
        alert.setConsecutiveDays(vo.getConsecutiveDays());
        alert.setAvgLoadRate(vo.getAvgLoadRate());
        alert.setAlertLevel("HIGH");
        alert.setStatus("ACTIVE");
        return alert;
    }
}