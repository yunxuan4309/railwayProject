package com.homework.railwayproject.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homework.railwayproject.mapper.LineSectionLoadRateMapMapper;
import com.homework.railwayproject.pojo.dto.LineSectionLoadRateQueryDTO;
import com.homework.railwayproject.pojo.vo.LineSectionLoadRateVO;
import com.homework.railwayproject.service.LineSectionLoadRateMapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LineSectionLoadRateMapServiceImpl implements LineSectionLoadRateMapService {

    @Autowired
    private LineSectionLoadRateMapMapper lineSectionLoadRateMapMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public IPage<LineSectionLoadRateVO> getLineSectionLoadRateMapWithPaging(LineSectionLoadRateQueryDTO query) {
        // 构建缓存键
        String cacheKey = buildCacheKey("line_section_load_rate_map", query);

        // 尝试从缓存获取数据
        IPage<LineSectionLoadRateVO> cachedResult = (IPage<LineSectionLoadRateVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            log.info("从缓存获取线路断面满载率地图数据，key: {}", cacheKey);
            return cachedResult;
        }

        // 创建分页对象
        Page<LineSectionLoadRateVO> page = new Page<>(query.getPage(), query.getSize());

        // 查询数据
        List<LineSectionLoadRateVO> result = lineSectionLoadRateMapMapper.selectLineSectionLoadRateMap(query, (query.getPage() - 1) * query.getSize(), query.getSize());
        
        // 计算总数
        Integer total = lineSectionLoadRateMapMapper.selectLineSectionLoadRateCount(query);

        // 构建分页结果
        IPage<LineSectionLoadRateVO> pageResult = new Page<>();
        pageResult.setRecords(result);
        pageResult.setTotal(total);
        pageResult.setSize(query.getSize());
        pageResult.setCurrent(query.getPage());
        pageResult.setPages(total != null ? (int) Math.ceil((double) total / query.getSize()) : 0);

        // 为每个结果设置timeRange
        for (LineSectionLoadRateVO vo : result) {
            if (vo.getHour() != null) {
                String hourStr = String.format("%02d", vo.getHour());
                vo.setTimeRange(hourStr + ":00-" + hourStr + ":59");
            }
        }

        // 设置缓存，2小时过期
        redisTemplate.opsForValue().set(cacheKey, pageResult, 2, TimeUnit.HOURS);
        log.info("线路断面满载率地图数据已缓存，key: {}, 总数: {}, 当前页数据条数: {}", cacheKey, total, result.size());

        return pageResult;
    }

    @Override
    public List<LineSectionLoadRateVO> getAllLines() {
        // 尝试从缓存获取数据
        String cacheKey = "all_lines_info";
        List<LineSectionLoadRateVO> cachedResult = (List<LineSectionLoadRateVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            log.info("从缓存获取线路信息，key: {}", cacheKey);
            return cachedResult;
        }

        // 查询数据
        List<LineSectionLoadRateVO> result = lineSectionLoadRateMapMapper.selectAllLines();

        // 设置缓存，2小时过期
        redisTemplate.opsForValue().set(cacheKey, result, 2, TimeUnit.HOURS);
        log.info("线路信息已缓存，key: {}, 数据条数: {}", cacheKey, result.size());

        return result;
    }

    /**
     * 构建缓存键
     */
    private String buildCacheKey(String prefix, LineSectionLoadRateQueryDTO query) {
        StringBuilder sb = new StringBuilder(prefix);
        sb.append(":").append(query.getLineCode() != null ? query.getLineCode() : "all");
        sb.append(":").append(query.getFlowDate() != null ? query.getFlowDate() : "all");
        sb.append(":").append(query.getStartDate() != null ? query.getStartDate() : "all");
        sb.append(":").append(query.getEndDate() != null ? query.getEndDate() : "all");
        sb.append(":").append(query.getStartHour() != null ? query.getStartHour() : "all");
        sb.append(":").append(query.getEndHour() != null ? query.getEndHour() : "all");
        sb.append(":").append(query.getStartStationId() != null ? query.getStartStationId() : "all");
        sb.append(":").append(query.getEndStationId() != null ? query.getEndStationId() : "all");
        sb.append(":").append(query.getStartStationName() != null ? query.getStartStationName() : "all");
        sb.append(":").append(query.getEndStationName() != null ? query.getEndStationName() : "all");
        sb.append(":page").append(query.getPage()).append(":size").append(query.getSize());
        
        return sb.toString();
    }
}