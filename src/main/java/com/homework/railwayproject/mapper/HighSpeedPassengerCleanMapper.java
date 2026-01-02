package com.homework.railwayproject.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homework.railwayproject.pojo.dto.TicketQueryDTO;
import com.homework.railwayproject.pojo.dto.TicketResultDTO;
import com.homework.railwayproject.pojo.entity.HighSpeedPassengerClean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface HighSpeedPassengerCleanMapper extends BaseMapper<HighSpeedPassengerClean> {
    int insertBatchSomeColumn(List<HighSpeedPassengerClean> passengers);

    /**
     * 统计清洗表中的所有数据量
     *
     * @return 数据总条数
     */
    Long countAllCleanData();
    
    /**
     * 根据条件查询票务数据（包含关联查询）
     *
     * @param page 分页参数
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<TicketResultDTO> queryTicketsWithConditions(Page<HighSpeedPassengerClean> page, TicketQueryDTO queryDTO);
    
    /**
     * 查询所有不重复的车票类型
     * 
     * @return 车票类型列表
     */
    List<Integer> selectDistinctTicketTypes();
    
    /**
     * 查询所有不重复的座位类型
     * 
     * @return 座位类型列表
     */
    List<String> selectDistinctSeatTypes();
    
    /**
     * 查询所有不重复的列车等级类型
     * 
     * @return 列车等级类型列表
     */
    List<String> selectDistinctTrainLevelTypes();
}