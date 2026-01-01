package com.homework.railwayproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        String cacheKey = "load:rate:" + (query.getLineCode() != null ? query.getLineCode() : "all")
                + ":" + query.getFlowDate();

        List<LoadRateVO> cachedResult = (List<LoadRateVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            log.info("从缓存获取区间满载率数据，key: {}", cacheKey);
            return cachedResult;
        }

        List<LoadRateVO> result = lineOptimizationMapper.selectSectionLoadRate(query);

        redisTemplate.opsForValue().set(cacheKey, result, 5, TimeUnit.MINUTES);
        log.info("区间满载率数据已缓存，key: {}", cacheKey);

        return result;
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

                if (loadRate > 90) {
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
            // 检查是否已有相同的告警
            OverloadAlert existing = lineOptimizationMapper.selectOverloadAlert(
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