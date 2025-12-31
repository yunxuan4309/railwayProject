package com.homework.railwayproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homework.railwayproject.mapper.LineMapper;
import com.homework.railwayproject.pojo.entity.Line;
import com.homework.railwayproject.service.LineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class LineServiceImpl extends ServiceImpl<LineMapper, Line> implements LineService {

    @Override
    public IPage<Line> getLinePage(Page<Line> page, String lineName, String lineType) {
        LambdaQueryWrapper<Line> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Line::getIsDeleted, 0);

        if (StringUtils.hasText(lineName)) {
            wrapper.like(Line::getLineName, lineName);
        }

        if (StringUtils.hasText(lineType)) {
            wrapper.like(Line::getLineType, lineType);
        }

        wrapper.orderByDesc(Line::getCreateTime);

        return page(page, wrapper);
    }

    @Override
    public Line getLineByCode(String lineCode) {
        if (lineCode == null) {
            return null;
        }
        LambdaQueryWrapper<Line> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Line::getLineCode, lineCode);
        wrapper.eq(Line::getIsDeleted, 0);
        return getOne(wrapper);
    }

    @Override
    public Line addLine(Line line) {
        if (line == null) {
            return null;
        }
        line.setIsDeleted(0);
        save(line);
        return line;
    }

    @Override
    public Line updateLine(Line line) {
        if (line == null || line.getLineCode() == null) {
            return null;
        }
        LambdaQueryWrapper<Line> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Line::getLineCode, line.getLineCode());
        wrapper.eq(Line::getIsDeleted, 0);

        Line existingLine = getOne(wrapper);
        if (existingLine == null) {
            return null;
        }

        update(line, wrapper);
        return line;
    }

    @Override
    public Boolean deleteLine(String lineCode) {
        if (lineCode == null) {
            return false;
        }
        LambdaQueryWrapper<Line> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Line::getLineCode, lineCode);
        wrapper.eq(Line::getIsDeleted, 0);

        Line line = getOne(wrapper);
        if (line == null) {
            return false;
        }

        line.setIsDeleted(1);
        return update(line, wrapper);
    }
}
