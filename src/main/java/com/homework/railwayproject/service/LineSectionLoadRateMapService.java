package com.homework.railwayproject.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homework.railwayproject.pojo.dto.LineSectionLoadRateQueryDTO;
import com.homework.railwayproject.pojo.vo.LineSectionLoadRateVO;

import java.util.List;

public interface LineSectionLoadRateMapService {
    /**
     * 获取线路断面满载率地图数据（带分页）
     */
    IPage<LineSectionLoadRateVO> getLineSectionLoadRateMapWithPaging(LineSectionLoadRateQueryDTO query);

    /**
     * 获取所有线路信息
     */
    List<LineSectionLoadRateVO> getAllLines();
}