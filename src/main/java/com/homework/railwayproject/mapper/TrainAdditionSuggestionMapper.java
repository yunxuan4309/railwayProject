package com.homework.railwayproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homework.railwayproject.pojo.entity.TrainAdditionSuggestion;
import com.homework.railwayproject.pojo.dto.TrainAdditionSuggestionQueryDTO;
import com.homework.railwayproject.pojo.vo.TrainAdditionSuggestionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 列车增车建议Mapper接口
 */
public interface TrainAdditionSuggestionMapper extends BaseMapper<TrainAdditionSuggestion> {
    /**
     * 根据条件分页查询增车建议
     */
    List<TrainAdditionSuggestionVO> selectSuggestionsByCondition(@Param("params") Map<String, Object> params);

    /**
     * 查询增车建议总数
     */
    Integer selectSuggestionsCount(@Param("query") TrainAdditionSuggestionQueryDTO query);
}