package com.homework.railwayproject.service.impl;

import com.homework.railwayproject.mapper.ODHeatMapMapper;
import com.homework.railwayproject.pojo.dto.ODHeatMapDTO;
import com.homework.railwayproject.pojo.entity.ODHeatMap;
import com.homework.railwayproject.service.ODHeatMapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ODHeatMapServiceImpl implements ODHeatMapService {

    @Autowired
    private ODHeatMapMapper odHeatMapMapper;

    @Override
    public List<ODHeatMap> getODHeatMapByDate(LocalDate date) {
        log.info("获取指定日期的OD热力图数据，日期: {}", date);
        List<ODHeatMap> result = odHeatMapMapper.selectODHeatMapByDate(date);
        log.info("查询结果数量: {}", result.size());
        return result;
    }

    @Override
    public List<ODHeatMap> getODHeatMapByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("获取日期范围的OD热力图数据，开始日期: {}，结束日期: {}", startDate, endDate);
        return odHeatMapMapper.selectODHeatMapByDateRange(startDate, endDate);
    }

    @Override
    public List<ODHeatMap> getODHeatMapByDepartureStation(Integer stationId, LocalDate date) {
        log.info("获取指定出发站点的OD热力图数据，站点ID: {}，日期: {}", stationId, date);
        return odHeatMapMapper.selectODHeatMapByDepartureStation(stationId, date);
    }

    @Override
    public List<ODHeatMap> getODHeatMapByArrivalStation(Integer stationId, LocalDate date) {
        log.info("获取指定到达站点的OD热力图数据，站点ID: {}，日期: {}", stationId, date);
        return odHeatMapMapper.selectODHeatMapByArrivalStation(stationId, date);
    }

    @Override
    public ODHeatMapDTO.ODHeatMapListDTO convertToDTO(List<ODHeatMap> odHeatMaps, String dateRange) {
        log.info("转换OD热力图数据为DTO，数据条数: {}", odHeatMaps.size());
        
        List<ODHeatMapDTO> dtoList = odHeatMaps.stream().map(od -> {
            ODHeatMapDTO dto = new ODHeatMapDTO();
            
            // 设置起始站点信息
            ODHeatMapDTO.StationInfo originStation = new ODHeatMapDTO.StationInfo();
            originStation.setStationId(od.getOriginStationId());
            originStation.setStationName(od.getOriginStationName());
            dto.setOriginStation(originStation);
            
            // 设置目标站点信息
            ODHeatMapDTO.StationInfo destStation = new ODHeatMapDTO.StationInfo();
            destStation.setStationId(od.getDestStationId());
            destStation.setStationName(od.getDestStationName());
            dto.setDestStation(destStation);
            
            // 设置其他信息
            dto.setPassengerFlow(od.getPassengerFlow());
            dto.setDateRange(od.getTravelDate());
            dto.setHeatValue(od.getHeatValue());
            
            return dto;
        }).collect(Collectors.toList());
        
        ODHeatMapDTO.ODHeatMapListDTO result = new ODHeatMapDTO.ODHeatMapListDTO();
        result.setOdData(dtoList);
        result.setDateRange(dateRange);
        result.setTotalRecords(dtoList.size());
        
        return result;
    }
}