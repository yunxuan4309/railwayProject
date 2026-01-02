package com.homework.railwayproject.mapper;

import com.homework.railwayproject.pojo.dto.LineSectionLoadRateQueryDTO;
import com.homework.railwayproject.pojo.vo.LineSectionLoadRateVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LineSectionLoadRateMapMapper {
    /**
     * 查询线路断面满载率地图数据
     */
    List<LineSectionLoadRateVO> selectLineSectionLoadRateMap(@Param("query") LineSectionLoadRateQueryDTO query, 
                                                            @Param("offset") int offset, 
                                                            @Param("size") int size);

    /**
     * 查询线路断面满载率数据总数
     */
    Integer selectLineSectionLoadRateCount(@Param("query") LineSectionLoadRateQueryDTO query);

    /**
     * 查询所有线路信息
     */
    List<LineSectionLoadRateVO> selectAllLines();
}