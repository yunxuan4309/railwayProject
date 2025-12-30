package com.homework.railwayproject.pojo.dto;

import com.homework.railwayproject.pojo.entity.HighSpeedPassengerClean;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 数据处理结果类
 * 
 * 用于内部数据处理过程的结果传递，主要在数据清洗过程中使用
 * 
 * 字段说明：
 * - cleanDataList: 清洗后的数据列表，包含通过4合1和部分批次处理生成的所有记录
 * - incompleteBatches: 不完整批次的数量，表示处理了多少个1-3条记录的组
 */
@Data
public class ProcessResult implements Serializable {
    private List<HighSpeedPassengerClean> cleanDataList;  // 清洗后的数据列表
    private int incompleteBatches;                        // 不完整批次数量
}