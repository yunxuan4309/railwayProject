package com.homework.railwayproject.service;

//Author:[谢云轩]
//QQ:[1721476339]
//ID:[632307060623]
//Date:2025/11/26
//Time:8:48

import com.homework.railwayproject.pojo.dto.ImportResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * 高铁乘客数据导入服务接口
 * @author 17214
 */
public interface HighSpeedPassengerImportService {
    /**
     * 批量导入CSV数据
     *
     * @param file CSV文件
     * @return 导入结果
     * @throws Exception 导入异常
     */
    ImportResult importCsvData(MultipartFile file) throws Exception;
    
    /**
     * 数据清洗功能
     * 从原始数据库表中读取数据进行清洗，并存入清洗数据表
     *
     * @param filename 文件名，用于生成版本号
     * @return 清洗结果
     */
    ImportResult cleanData(String filename);
}