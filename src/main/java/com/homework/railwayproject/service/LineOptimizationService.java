package com.homework.railwayproject.service;

import com.homework.railwayproject.pojo.dto.SectionLoadRateQueryDTO;
import com.homework.railwayproject.pojo.vo.LoadRateVO;
import com.homework.railwayproject.pojo.vo.OverloadAlertVO;
import com.homework.railwayproject.pojo.vo.TrainAdditionSuggestionVO;

import java.time.LocalDate;
import java.util.List;

public interface LineOptimizationService {

    /**
     * 计算区间满载率（带5分钟缓存）
     */
    List<LoadRateVO> calculateSectionLoadRate(SectionLoadRateQueryDTO query);

    /**
     * 获取过载告警（连续7天上座率>90%）
     */
    List<OverloadAlertVO> getOverloadAlerts();

    /**
     * 生成加车建议
     */
    List<TrainAdditionSuggestionVO> generateAdditionSuggestions();

    /**
     * 计算并保存区间统计结果（每日执行）
     */
    void calculateAndSaveSectionStatistics(LocalDate flowDate);

    /**
     * 检测连续过载区间（每日执行）
     */
    void detectContinuousOverloadSections(LocalDate checkDate);
}