package com.homework.railwayproject.mapper;

import com.homework.railwayproject.pojo.dto.StationStats;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

public interface StationRoleMapper {
    /**
     * 统计指定站点在指定日期的客流数据
     *
     * @param stationId    站点ID
     * @param analysisDate 分析日期
     * @return 站点统计数据
     */
    StationStats selectStationStats(@Param("stationId") Integer stationId,
                                    @Param("analysisDate") LocalDate analysisDate);
}
