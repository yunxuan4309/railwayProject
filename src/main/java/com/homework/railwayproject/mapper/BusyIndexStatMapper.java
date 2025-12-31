package com.homework.railwayproject.mapper;

import com.homework.railwayproject.pojo.entity.BusyIndexStat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 繁忙指数统计Mapper接口
 */
@Mapper
public interface BusyIndexStatMapper {

    /**
     * 查询最近一段时间内各站点的繁忙指数前20名
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 繁忙指数统计列表
     */
    List<BusyIndexStat> selectTop20BusyIndexStations(@Param("startTime") LocalDate startTime,
                                                     @Param("endTime") LocalDate endTime);

    /**
     * 查询指定站点指定时间段的繁忙指数
      * @param siteId
     * @param startTime
     * @param endTime
     * @return
     */
    BusyIndexStat selectBusyIndexStatByIdAndTime(@Param("siteId") Integer siteId, @Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime);
}