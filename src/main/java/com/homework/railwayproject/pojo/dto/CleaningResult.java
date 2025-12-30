package com.homework.railwayproject.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据清洗结果统计类
 * 
 * 用于统计和返回数据清洗操作的结果信息，包含详细的处理统计信息
 * 
 * 字段说明：
 * - totalProcessed: 总处理的原始数据条数（状态为0的未清洗数据）
 * - successCount: 成功清洗并插入清洗表的数据条数
 * - failedCount: 清洗过程中失败的数据条数
 * - skippedCount: 跳过的数据条数（状态为3的重复数据）
 * - incompleteBatches: 不完整批次的数量（剩余1-3条记录的组）
 * - message: 处理结果的详细描述信息
 */
@Data
public class CleaningResult implements Serializable {
    private int totalProcessed;      // 总处理条数
    private int successCount;        // 成功清洗条数
    private int failedCount;         // 失败条数
    private int skippedCount;        // 跳过条数（重复数据）
    private int incompleteBatches;   // 不完整批次数量（剩余1-3条记录的组）
    private String message;          // 处理结果描述
}