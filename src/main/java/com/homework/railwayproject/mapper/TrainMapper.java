package com.homework.railwayproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homework.railwayproject.pojo.entity.Train;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrainMapper extends BaseMapper<Train> {
}
