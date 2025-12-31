package com.homework.railwayproject.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homework.railwayproject.pojo.entity.Line;

public interface LineService {

    IPage<Line> getLinePage(Page<Line> page, String lineName, String lineType);

    Line getLineByCode(String lineCode);

    Line addLine(Line line);

    Line updateLine(Line line);

    Boolean deleteLine(String lineCode);
}
