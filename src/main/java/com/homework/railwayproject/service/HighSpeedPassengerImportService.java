package com.homework.railwayproject.service;

import com.homework.railwayproject.pojo.dto.CleaningResult;
import com.homework.railwayproject.pojo.dto.ImportResult;
import org.springframework.web.multipart.MultipartFile;

public interface HighSpeedPassengerImportService {
    /**
     * 批量导入CSV数据
     * 
     * 该方法用于导入高铁客运原始数据CSV文件，支持重复数据检测和标记功能
     * 
     * 处理流程：
     * 1. 解析CSV文件内容
     * 2. 检测原始数据中的重复ID
     * 3. 重复数据将被插入但状态标记为3（重复）
     * 4. 非重复数据状态标记为0（未清洗）
     * 5. 批量插入数据库
     * 
     * @param file 上传的CSV文件，应包含高铁客运原始数据
     * @return 导入结果，包含成功、失败的记录数统计
     * @throws Exception 导入过程中可能出现的异常
     * 
     * 业务规则：
     * - 重复ID的数据会被插入但状态设为3
     * - 检查数据库中是否已存在相同ID
     * - 使用原始ID作为数据标识
     */
    ImportResult importCsvData(MultipartFile file) throws Exception;
    
    /**
     * 数据清洗功能
     * 
     * 从原始数据库表中读取数据进行清洗，并存入清洗数据表
     * 
     * 清洗策略（方案四：混合策略）：
     * 1. 跳过状态为3的重复数据
     * 2. 优先处理完整的4条记录组，执行4合1合并策略
     * 3. 对于剩余的1-3条记录，根据信息类型单独转换为清洗记录
     * 4. 保留所有原始数据，不丢弃任何记录
     * 5. 生成清洗后的数据版本号
     * 
     * @param filename 文件名，用于生成数据版本号（格式：v{版本号}_{数据源}_{日期}_{文件名}）
     * @return 清洗结果，包含处理总数、成功数、失败数、跳过数和不完整批次数
     * 
     * 业务规则：
     * - 只处理状态为0的未清洗数据
     * - 跳过状态为3的重复数据
     * - 不完整批次（1-3条记录）也会被处理
     * - 生成唯一的数据版本号用于追溯
     */
    CleaningResult cleanData(String filename);
}