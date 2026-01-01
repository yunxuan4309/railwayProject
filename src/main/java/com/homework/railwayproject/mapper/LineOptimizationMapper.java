package com.homework.railwayproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homework.railwayproject.pojo.dto.SectionLoadRateQueryDTO;
import com.homework.railwayproject.pojo.entity.OverloadAlert;
import com.homework.railwayproject.pojo.entity.SectionDailyFlow;
import com.homework.railwayproject.pojo.entity.SectionHourlyFlow;
import com.homework.railwayproject.pojo.vo.LoadRateVO;
import com.homework.railwayproject.pojo.vo.OverloadAlertVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface LineOptimizationMapper extends BaseMapper<SectionHourlyFlow> {

    // 1. 查询区间满载率
    List<LoadRateVO> selectSectionLoadRate(@Param("query") SectionLoadRateQueryDTO query);

    // 2. 查询区间每小时客流数据
    List<SectionHourlyFlow> selectSectionHourlyFlow(
            @Param("lineCode") String lineCode,
            @Param("flowDate") LocalDate flowDate);

    // 3. 查询指定区间小时数据
    SectionHourlyFlow selectHourlyFlow(
            @Param("lineCode") String lineCode,
            @Param("startStationId") Integer startStationId,
            @Param("endStationId") Integer endStationId,
            @Param("flowDate") LocalDate flowDate,
            @Param("hour") Integer hour);

    // 4. 计算每小时统计数据
    List<SectionHourlyFlow> calculateSectionHourlyFlow(
            @Param("flowDate") LocalDate flowDate,
            @Param("hour") Integer hour);

    // 5. 计算每日统计数据
    List<SectionDailyFlow> calculateSectionDailyFlow(@Param("flowDate") LocalDate flowDate);

    // 6. 查询每日数据
    SectionDailyFlow selectDailyFlow(
            @Param("lineCode") String lineCode,
            @Param("startStationId") Integer startStationId,
            @Param("endStationId") Integer endStationId,
            @Param("flowDate") LocalDate flowDate);

    // 7. 插入每日数据
    int insertDailyFlow(SectionDailyFlow flow);

    // 8. 更新每日数据
    int updateDailyFlow(SectionDailyFlow flow);

    // 9. 查询活跃状态的过载告警
    List<OverloadAlertVO> selectActiveOverloadAlerts();

    // 10. 查询连续过载区间
    List<OverloadAlertVO> selectContinuousOverloadSections(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("consecutiveDays") int consecutiveDays,
            @Param("threshold") double threshold);

    // 11. 查询过载告警
    OverloadAlert selectOverloadAlert(
            @Param("lineCode") String lineCode,
            @Param("startStationId") Integer startStationId,
            @Param("endStationId") Integer endStationId,
            @Param("alertStartDate") LocalDate alertStartDate,
            @Param("alertEndDate") LocalDate alertEndDate);

    // 12. 插入过载告警
    int insertOverloadAlert(OverloadAlert alert);

    // 13. 查询区间高峰时段
    List<Map<String, Object>> selectPeakHoursBySection(
            @Param("lineCode") String lineCode,
            @Param("startStationId") Integer startStationId,
            @Param("endStationId") Integer endStationId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 14. 更新过载告警状态
    int updateOverloadAlertStatus(@Param("id") Long id, @Param("status") String status);

    // 15. 清理旧的统计数据
    void cleanOldStatistics();
}