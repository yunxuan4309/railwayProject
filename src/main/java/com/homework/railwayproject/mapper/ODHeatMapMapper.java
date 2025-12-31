package com.homework.railwayproject.mapper;

import com.homework.railwayproject.pojo.entity.ODHeatMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ODHeatMapMapper {

    /**
     * 获取指定日期的OD热力图数据
     *
     * @param date 指定日期
     * @return OD热力图数据列表
     */
    List<ODHeatMap> selectODHeatMapByDate(@Param("date") LocalDate date);

    /**
     * 获取指定日期范围的OD热力图数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return OD热力图数据列表
     */
    List<ODHeatMap> selectODHeatMapByDateRange(@Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);

    /**
     * 获取指定站点的出发客流数据
     *
     * @param stationId 站点ID
     * @param date 指定日期
     * @return OD热力图数据列表
     */
    List<ODHeatMap> selectODHeatMapByDepartureStation(@Param("stationId") Integer stationId,
                                                      @Param("date") LocalDate date);

    /**
     * 获取指定站点的到达客流数据
     *
     * @param stationId 站点ID
     * @param date 指定日期
     * @return OD热力图数据列表
     */
    List<ODHeatMap> selectODHeatMapByArrivalStation(@Param("stationId") Integer stationId,
                                                    @Param("date") LocalDate date);
}