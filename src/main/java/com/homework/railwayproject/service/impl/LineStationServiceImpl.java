package com.homework.railwayproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homework.railwayproject.mapper.LineStationMapper;
import com.homework.railwayproject.pojo.entity.LineStation;
import com.homework.railwayproject.service.LineStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class LineStationServiceImpl extends ServiceImpl<LineStationMapper, LineStation> implements LineStationService {

    @Override
    public IPage<LineStation> getLineStationPage(Page<LineStation> page, String lineCode, Integer siteId) {
        LambdaQueryWrapper<LineStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LineStation::getIsDeleted, 0);

        if (StringUtils.hasText(lineCode)) {
            wrapper.eq(LineStation::getLineCode, lineCode);
        }

        if (siteId != null) {
            wrapper.eq(LineStation::getSiteId, siteId);
        }

        wrapper.orderByDesc(LineStation::getCreateTime);

        return page(page, wrapper);
    }

    @Override
    public LineStation getLineStationById(Long id) {
        if (id == null) {
            return null;
        }
        LambdaQueryWrapper<LineStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LineStation::getId, id);
        wrapper.eq(LineStation::getIsDeleted, 0);
        return getOne(wrapper);
    }

    @Override
    public LineStation addLineStation(LineStation lineStation) {
        if (lineStation == null) {
            return null;
        }
        lineStation.setIsDeleted(0);
        save(lineStation);
        return lineStation;
    }

    @Override
    public LineStation updateLineStation(LineStation lineStation) {
        if (lineStation == null || lineStation.getId() == null) {
            return null;
        }
        LambdaQueryWrapper<LineStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LineStation::getId, lineStation.getId());
        wrapper.eq(LineStation::getIsDeleted, 0);

        LineStation existingLineStation = getOne(wrapper);
        if (existingLineStation == null) {
            return null;
        }

        update(lineStation, wrapper);
        return lineStation;
    }

    @Override
    public Boolean deleteLineStation(Long id) {
        if (id == null) {
            return false;
        }
        LambdaQueryWrapper<LineStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LineStation::getId, id);
        wrapper.eq(LineStation::getIsDeleted, 0);

        LineStation lineStation = getOne(wrapper);
        if (lineStation == null) {
            return false;
        }

        lineStation.setIsDeleted(1);
        return update(lineStation, wrapper);
    }
}