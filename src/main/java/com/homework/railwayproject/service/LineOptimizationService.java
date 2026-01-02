package com.homework.railwayproject.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homework.railwayproject.pojo.dto.SectionLoadRateQueryDTO;
import com.homework.railwayproject.pojo.vo.LoadRateVO;
import com.homework.railwayproject.pojo.vo.OverloadAlertVO;
import com.homework.railwayproject.pojo.vo.TrainAdditionSuggestionVO;
import java.time.LocalDate;
import java.util.List;

public interface LineOptimizationService {

    /**
     * 计算区间满载率（带2小时缓存）
     */
    List<LoadRateVO> calculateSectionLoadRate(SectionLoadRateQueryDTO query);

    /**
     * 从清洗表直接计算区间满载率（带缓存）
     */
    List<LoadRateVO> calculateSectionLoadRateFromCleanData(SectionLoadRateQueryDTO query);

    // 删除这个方法的定义
    // /**
    //  * 从清洗表直接计算区间满载率（带5分钟缓存）
    //  */
    // List<LoadRateVO> calculateSectionLoadRateFromCleanData(String lineCode, LocalDate flowDate);

    /**
     * 从清洗表直接计算区间满载率（带分页）
     */
    List<LoadRateVO> calculateSectionLoadRateWithPaging(SectionLoadRateQueryDTO query);
    
    /**
     * 获取区间满载率数据总数（用于分页）
     */
    Integer getSectionLoadRateCount(SectionLoadRateQueryDTO query);
    
    /**
     * 获取分页的区间满载率数据
     */
    IPage<LoadRateVO> getSectionLoadRateWithPaging(SectionLoadRateQueryDTO query);

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