package com.homework.railwayproject.service;

import com.homework.railwayproject.pojo.dto.StatisticsResult;
import com.homework.railwayproject.pojo.entity.Line;
import com.homework.railwayproject.pojo.entity.Station;

import java.util.List;

/**
 * 数据统计服务接口
 */
public interface DataSumService {

    /**
     * 统计清洗表中的数据量
     *
     * @return 统计结果
     */
    StatisticsResult countCleanData();

    /**
     * 统计线路数量
     *
     * @return 统计结果
     */
    StatisticsResult countLines();

    /**
     * 统计站点数量
     *
     * @return 统计结果
     */
    StatisticsResult countStations();
}