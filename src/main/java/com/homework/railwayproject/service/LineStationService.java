package com.homework.railwayproject.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homework.railwayproject.pojo.entity.LineStation;

public interface LineStationService extends IService<LineStation> {
    IPage<LineStation> getLineStationPage(Page<LineStation> page, String lineCode, Integer siteId);

    LineStation getLineStationById(Long id);

    LineStation addLineStation(LineStation lineStation);

    LineStation updateLineStation(LineStation lineStation);

    Boolean deleteLineStation(Long id);
}