package com.homework.railwayproject.service;

import com.homework.railwayproject.pojo.entity.BusyIndexStat;

import java.time.LocalDate;
import java.util.List;

/**
 * 繁忙指数统计服务接口
 */
public interface BusyIndexStatService {

    /**
     * 获取站点繁忙指数前20名
     *
     * @return 站点繁忙指数统计列表
     */
    List<BusyIndexStat> getTop20BusyIndexStations();

    /**
     * 定时任务：统计并缓存站点繁忙指数数据
     */
    void calculateAndCacheTop20BusyIndexStations();
/**
 * 根据站点ID和开始结束时间获取站点繁忙指数统计信息
 *
 * @param siteId 站点ID
 * @param startTime 开始时间
 * @param endTime 结束时间
 * @return 站点繁忙指数统计信息
 */
    BusyIndexStat getBusyIndexStatByIdAndTime(Integer siteId, LocalDate startTime, LocalDate endTime);
}