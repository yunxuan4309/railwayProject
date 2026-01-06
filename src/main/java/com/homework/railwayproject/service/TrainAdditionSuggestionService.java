package com.homework.railwayproject.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homework.railwayproject.pojo.entity.TrainAdditionSuggestion;
import com.homework.railwayproject.pojo.dto.TrainAdditionSuggestionQueryDTO;
import com.homework.railwayproject.pojo.vo.TrainAdditionSuggestionVO;

/**
 * 列车增车建议服务接口
 */
public interface TrainAdditionSuggestionService extends IService<TrainAdditionSuggestion> {
    /**
     * 分页查询增车建议
     */
    Page<TrainAdditionSuggestionVO> getSuggestionsByCondition(TrainAdditionSuggestionQueryDTO query);

    /**
     * 保存增车建议
     */
    TrainAdditionSuggestionVO saveSuggestion(TrainAdditionSuggestionVO suggestion);

    /**
     * 更新增车建议状态
     */
    TrainAdditionSuggestionVO updateSuggestionStatus(Long id, String status);

    /**
     * 删除增车建议
     */
    boolean deleteSuggestion(Long id);
}