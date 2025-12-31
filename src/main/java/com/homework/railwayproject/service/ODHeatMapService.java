package com.homework.railwayproject.service;

import com.homework.railwayproject.pojo.dto.ODHeatMapDTO;
import com.homework.railwayproject.pojo.entity.ODHeatMap;

import java.time.LocalDate;
import java.util.List;

public interface ODHeatMapService {

    /**
     * 获取指定日期的OD热力图数据
     *
     * @param date 指定日期
     * @return OD热力图数据列表
     */
    List<ODHeatMap> getODHeatMapByDate(LocalDate date);

    /**
     * 获取指定日期范围的OD热力图数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return OD热力图数据列表
     */
    List<ODHeatMap> getODHeatMapByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 获取指定站点的出发客流OD热力图数据
     *
     * @param stationId 站点ID
     * @param date 指定日期
     * @return OD热力图数据列表
     */
    List<ODHeatMap> getODHeatMapByDepartureStation(Integer stationId, LocalDate date);

    /**
     * 获取指定站点的到达客流OD热力图数据
     *
     * @param stationId 站点ID
     * @param date 指定日期
     * @return OD热力图数据列表
     */
    List<ODHeatMap> getODHeatMapByArrivalStation(Integer stationId, LocalDate date);

    /**
     * 将实体类转换为DTO
     *
     * @param odHeatMaps 实体类列表
     * @return DTO列表
     */
    ODHeatMapDTO.ODHeatMapListDTO convertToDTO(List<ODHeatMap> odHeatMaps, String dateRange);
}