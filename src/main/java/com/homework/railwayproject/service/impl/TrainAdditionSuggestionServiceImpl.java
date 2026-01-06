package com.homework.railwayproject.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homework.railwayproject.mapper.TrainAdditionSuggestionMapper;
import com.homework.railwayproject.pojo.entity.TrainAdditionSuggestion;
import com.homework.railwayproject.pojo.dto.TrainAdditionSuggestionQueryDTO;
import com.homework.railwayproject.pojo.vo.TrainAdditionSuggestionVO;
import com.homework.railwayproject.service.TrainAdditionSuggestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 列车增车建议服务实现类
 */
@Slf4j
@Service
@Transactional
public class TrainAdditionSuggestionServiceImpl extends ServiceImpl<TrainAdditionSuggestionMapper, TrainAdditionSuggestion> 
        implements TrainAdditionSuggestionService {

    @Autowired
    private TrainAdditionSuggestionMapper trainAdditionSuggestionMapper;

    @Override
    public Page<TrainAdditionSuggestionVO> getSuggestionsByCondition(TrainAdditionSuggestionQueryDTO query) {
        // 计算偏移量
        int offset = (query.getCurrent() - 1) * query.getSize();
        
        // 查询总数
        Integer total = trainAdditionSuggestionMapper.selectSuggestionsCount(query);
        
        // 查询分页数据 - 使用Map传递参数，包括offset
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        params.put("offset", offset);
        
        // 直接调用Mapper方法进行分页查询，使用正确的参数传递
        List<TrainAdditionSuggestionVO> records = trainAdditionSuggestionMapper.selectSuggestionsByCondition(params);
        
        // 构建分页结果
        Page<TrainAdditionSuggestionVO> result = new Page<>();
        result.setRecords(records);
        result.setTotal(total != null ? total.longValue() : 0);
        result.setSize(query.getSize());
        result.setCurrent(query.getCurrent());
        result.setPages(total != null ? (long) Math.ceil((double) total / query.getSize()) : 0);
        
        return result;
    }

    @Override
    public TrainAdditionSuggestionVO saveSuggestion(TrainAdditionSuggestionVO suggestion) {
        TrainAdditionSuggestion entity = new TrainAdditionSuggestion();
        BeanUtils.copyProperties(suggestion, entity);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setIsDeleted(0);
        
        // 如果是系统生成的建议，默认状态为待处理
        if (entity.getStatus() == null) {
            entity.setStatus("PENDING");
        }
        
        // 如果没有指定创建者类型，默认为系统生成
        if (entity.getCreatedBy() == null) {
            entity.setCreatedBy("SYSTEM");
        }
        
        // 保存到数据库
        this.save(entity);
        
        // 返回保存后的建议
        TrainAdditionSuggestionVO result = new TrainAdditionSuggestionVO();
        BeanUtils.copyProperties(entity, result);
        
        log.info("保存增车建议成功: 线路={}, 区间={}", entity.getLineCode(), entity.getSection());
        return result;
    }

    @Override
    public TrainAdditionSuggestionVO updateSuggestionStatus(Long id, String status) {
        TrainAdditionSuggestion suggestion = this.getById(id);
        if (suggestion == null) {
            log.error("更新增车建议状态失败：找不到ID为{}的建议", id);
            return null;
        }
        
        suggestion.setStatus(status);
        suggestion.setUpdateTime(LocalDateTime.now());
        
        boolean result = this.updateById(suggestion);
        if (result) {
            log.info("更新增车建议状态成功: ID={}, 新状态={}", id, status);
            
            // 返回更新后的建议
            TrainAdditionSuggestionVO vo = new TrainAdditionSuggestionVO();
            org.springframework.beans.BeanUtils.copyProperties(suggestion, vo);
            return vo;
        } else {
            log.error("更新增车建议状态失败: ID={}", id);
            return null;
        }
    }

    @Override
    public boolean deleteSuggestion(Long id) {
        TrainAdditionSuggestion suggestion = this.getById(id);
        if (suggestion == null) {
            log.error("删除增车建议失败：找不到ID为{}的建议", id);
            return false;
        }
        
        // 逻辑删除
        suggestion.setIsDeleted(1);
        suggestion.setUpdateTime(LocalDateTime.now());
        
        boolean result = this.updateById(suggestion);
        log.info("删除增车建议成功: ID={}", id);
        return result;
    }
    

}