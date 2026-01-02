package com.homework.railwayproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homework.railwayproject.pojo.dto.SectionLoadRateQueryDTO;
import com.homework.railwayproject.pojo.entity.OverloadAlert;
import com.homework.railwayproject.pojo.entity.SectionDailyFlow;
import com.homework.railwayproject.pojo.entity.SectionHourlyFlow;
import com.homework.railwayproject.pojo.vo.LoadRateVO;
import com.homework.railwayproject.pojo.vo.OverloadAlertVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface LineOptimizationMapper extends BaseMapper<SectionHourlyFlow> {

    // 原有方法
    List<LoadRateVO> selectSectionLoadRate(@Param("query") SectionLoadRateQueryDTO query);

    List<SectionHourlyFlow> selectSectionHourlyFlow(@Param("lineCode") String lineCode, @Param("flowDate") LocalDate flowDate);

    SectionHourlyFlow selectHourlyFlow(@Param("lineCode") String lineCode,
                                       @Param("startStationId") Integer startStationId,
                                       @Param("endStationId") Integer endStationId,
                                       @Param("flowDate") LocalDate flowDate,
                                       @Param("hour") Integer hour);

    void insertSectionHourlyFlow(SectionHourlyFlow flow);

    void deleteSectionHourlyFlowByDate(@Param("flowDate") LocalDate flowDate);

    List<SectionHourlyFlow> calculateSectionHourlyFlow(@Param("flowDate") LocalDate flowDate, @Param("hour") Integer hour);

    List<SectionDailyFlow> calculateSectionDailyFlow(@Param("flowDate") LocalDate flowDate);

    SectionDailyFlow selectDailyFlow(@Param("lineCode") String lineCode,
                                     @Param("startStationId") Integer startStationId,
                                     @Param("endStationId") Integer endStationId,
                                     @Param("flowDate") LocalDate flowDate);

    void insertDailyFlow(SectionDailyFlow flow);

    void updateDailyFlow(SectionDailyFlow flow);

    List<OverloadAlertVO> selectActiveOverloadAlerts();

    List<OverloadAlertVO> selectContinuousOverloadSections(@Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate,
                                                           @Param("consecutiveDays") Integer consecutiveDays,
                                                           @Param("threshold") Double threshold);

    OverloadAlertVO selectOverloadAlert(@Param("lineCode") String lineCode,
                                        @Param("startStationId") Integer startStationId,
                                        @Param("endStationId") Integer endStationId,
                                        @Param("alertStartDate") LocalDate alertStartDate,
                                        @Param("alertEndDate") LocalDate alertEndDate);

    void insertOverloadAlert(OverloadAlert alert);

    List<Map<String, Object>> selectPeakHoursBySection(@Param("lineCode") String lineCode,
                                                       @Param("startStationId") Integer startStationId,
                                                       @Param("endStationId") Integer endStationId,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    void updateOverloadAlertStatus(@Param("id") Long id, @Param("status") String status);

    void cleanOldStatistics();

    // 新增方法：直接从清洗表查询满载率数据
    List<LoadRateVO> selectSectionLoadRateFromCleanData(@Param("query") SectionLoadRateQueryDTO query);
    
    // 新增方法：支持分页查询满载率数据
    List<LoadRateVO> selectSectionLoadRateFromCleanDataWithPaging(@Param("params") Map<String, Object> params);
    
    // 新增方法：查询满载率数据总数
    Integer selectSectionLoadRateCount(@Param("query") SectionLoadRateQueryDTO query);
}