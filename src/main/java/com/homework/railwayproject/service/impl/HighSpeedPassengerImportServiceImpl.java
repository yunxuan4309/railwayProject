package com.homework.railwayproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.homework.railwayproject.mapper.HighSpeedPassengerMapper;
import com.homework.railwayproject.mapper.HighSpeedPassengerCleanMapper;
import com.homework.railwayproject.pojo.dto.CleaningResult;
import com.homework.railwayproject.pojo.dto.ImportResult;
import com.homework.railwayproject.pojo.dto.ProcessResult;
import com.homework.railwayproject.pojo.entity.HighSpeedPassenger;
import com.homework.railwayproject.pojo.entity.HighSpeedPassengerClean;
import com.homework.railwayproject.service.HighSpeedPassengerImportService;
import com.homework.railwayproject.util.csvUtil.CsvDataPreprocessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HighSpeedPassengerImportServiceImpl implements HighSpeedPassengerImportService {

    @Autowired
    private HighSpeedPassengerMapper highSpeedPassengerMapper;
    
    @Autowired
    private HighSpeedPassengerCleanMapper highSpeedPassengerCleanMapper;
    
    @Value("${app.data-version:1.1}")
    private String fixedVersion;

    @Autowired
    private CsvDataPreprocessor csvDataPreprocessor;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    @Transactional
    public ImportResult importCsvData(MultipartFile file) {
        ImportResult result = new ImportResult();
        List<HighSpeedPassenger> passengerList = new ArrayList<>();
        Set<Long> processedIds = new HashSet<>(); // 用于检测重复ID

        try {
            log.info("开始解析CSV文件，文件名: {}，大小: {} bytes",
                    file.getOriginalFilename(), file.getSize());

            List<Map<String, String>> csvData = parseCsvFile(file);
            log.info("成功解析 {} 行数据，开始导入处理", csvData.size());

            // 跳过数据验证步骤，直接进行转换
            // csvDataPreprocessor.validateCsvData(csvData);

            // 转换为实体对象
            int successCount = 0;
            int failCount = 0;
            int duplicateCount = 0; // 重复记录计数
            
            // 设置进度日志间隔
            int totalRows = csvData.size();
            int logInterval = Math.max(1, totalRows / 10); // 每处理10%记录一次日志

            for (int i = 0; i < csvData.size(); i++) {
                Map<String, String> row = csvData.get(i);
                try {
                    HighSpeedPassenger passenger = convertToEntity(row);
                    Long currentId = passenger.getId();
                    
                    // 验证关键字段
                    if (passenger.getOperationDate() == null) {
                        log.warn("第{}行数据的运行日期为空，跳过此记录", i + 3); // +3因为跳过了前2行，且索引从0开始
                        failCount++;
                        continue; // 跳过此记录
                    }
                    
                    // 检查ID是否重复
                    if (currentId != null && processedIds.contains(currentId)) {
                        // ID重复，设置状态为3（重复数据）
                        // 注意：虽然设置了状态为3，但在当前实现中，重复数据最终不会被插入数据库
                        // 这是为了保持代码的灵活性，以备将来需要保留重复数据记录的场景
                        passenger.setStatus(3);
                        duplicateCount++;
                    } else if (currentId != null && checkIdExistsInDatabase(currentId)) {
                        // 检查数据库中是否已存在此ID
                        // 注意：虽然设置了状态为3，但在当前实现中，重复数据最终不会被插入数据库
                        // 这是为了保持代码的灵活性，以备将来需要保留重复数据记录的场景
                        passenger.setStatus(3);
                        duplicateCount++;
                    } else {
                        // 首次出现的ID，设置为未清洗状态
                        passenger.setStatus(0);
                        if (currentId != null) {
                            processedIds.add(currentId);
                        }
                        successCount++;
                    }
                    
                    // 重复数据（状态为3）和非重复数据（状态为0）都会被添加到待插入列表
                    // 但在批量插入过程中，由于数据库约束或其他原因，重复数据可能无法成功插入
                    // 这种设计保持了代码的灵活性，以备将来需要保留重复数据记录的场景
                    passengerList.add(passenger);
                    
                    // 每隔一定数量记录输出一次进度
                    if (i > 0 && i % logInterval == 0) {
                        log.info("数据处理进度: {}/{} ({}%)", i, totalRows, (int)((double)i / totalRows * 100));
                    }

                } catch (Exception e) {
                    failCount++;
                }
            }

            log.info("数据转换完成 - 成功: {} 行，失败: {} 行，重复: {} 行", successCount, failCount, duplicateCount);

            // 批量插入数据库
            int insertedCount = batchInsertPassengers(passengerList);
            result.setSuccessCount(insertedCount);
            result.setFailedCount(failCount + duplicateCount + (passengerList.size() - insertedCount));

            log.info("导入完成，成功插入: {} 条，总失败: {} 条",
                    result.getSuccessCount(), result.getFailedCount());

            return result;
        } catch (Exception e) {
            log.error("导入失败", e);
            throw new RuntimeException("导入失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public CleaningResult cleanData(String filename) {
        CleaningResult result = new CleaningResult();
        
        try {
            log.info("开始数据清洗，文件名: {}", filename);
            
            // 查询所有未清洗的原始数据（status = 0）和跳过的重复数据（status = 3）
            // 注意：在当前实现中，由于重复数据在导入阶段通常不会被插入数据库，
            // 所以这里查询到的status=3的数据通常为0
            QueryWrapper<HighSpeedPassenger> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("status", 0, 3); // 包含未清洗和重复数据
            List<HighSpeedPassenger> rawDataList = highSpeedPassengerMapper.selectList(queryWrapper);
            log.info("查询到 {} 条原始数据（包含未清洗和重复数据）", rawDataList.size());
            
            // 分离不同状态的数据
            List<HighSpeedPassenger> toProcessList = rawDataList.stream()
                .filter(p -> p.getStatus() == 0) // 未清洗的数据
                .collect(Collectors.toList());
            
            int skippedCount = (int) rawDataList.stream()
                .filter(p -> p.getStatus() == 3) // 重复数据
                .count();
            
            log.info("需要处理的未清洗数据: {} 条，跳过的重复数据: {} 条", toProcessList.size(), skippedCount);
            
            if (toProcessList.isEmpty()) {
                log.info("没有需要清洗的数据");
                result.setTotalProcessed(0);
                result.setSuccessCount(0);
                result.setFailedCount(0);
                result.setSkippedCount(skippedCount);
                result.setMessage("没有需要清洗的数据");
                return result;
            }
            
            // 生成版本号：v{fixedVersion}_{数据源}_{日期}_{filename}
            String version = generateVersion(filename);
            
            // 按照清洗规则进行数据清洗
            ProcessResult processResult = processDataCleaning(toProcessList, version);
            List<HighSpeedPassengerClean> cleanDataList = processResult.getCleanDataList();
            int incompleteBatches = processResult.getIncompleteBatches();
            
            log.info("清洗后得到 {} 条数据，不完整批次: {} 个", cleanDataList.size(), incompleteBatches);
            
            // 批量插入清洗后的数据
            int insertedCount = batchInsertCleanPassengers(cleanDataList);
            int failedCount = cleanDataList.size() - insertedCount;
            
            // 只更新未清洗数据的状态为已清洗（status = 1），保留重复数据的状态（status = 3）
            updateRawDataStatus(toProcessList);
            
            // 设置结果统计
            result.setTotalProcessed(toProcessList.size());
            result.setSuccessCount(insertedCount);
            result.setFailedCount(failedCount);
            result.setSkippedCount(skippedCount);
            result.setIncompleteBatches(incompleteBatches);
            result.setMessage(String.format("清洗完成，处理了 %d 条原始数据，成功清洗 %d 条，失败 %d 条，跳过 %d 条重复数据，处理了 %d 个不完整批次", 
                toProcessList.size(), insertedCount, failedCount, skippedCount, incompleteBatches));
            
            log.info("清洗完成，成功插入: {} 条，失败: {} 条，跳过: {} 条，不完整批次: {} 个",
                    result.getSuccessCount(), result.getFailedCount(), result.getSkippedCount(), result.getIncompleteBatches());
                    
            return result;
        } catch (Exception e) {
            log.error("清洗失败", e);
            throw new RuntimeException("清洗失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 更新原始数据的状态为已清洗
     * 
     * @param rawDataList 原始数据列表
     */
    private void updateRawDataStatus(List<HighSpeedPassenger> rawDataList) {
        int updatedCount = 0;
        
        // 收集所有需要更新的数据ID，然后批量更新状态
        List<Long> idsToBeUpdated = new ArrayList<>();
        for (HighSpeedPassenger passenger : rawDataList) {
            // 只更新状态为0（未清洗）的数据为1（已清洗），保留状态为3（重复）的数据不变
            // 注意：在当前实现中，rawDataList参数通常只包含status=0的数据，
            // 因为在调用此方法前已经通过filter筛选了status=0的数据
            if (passenger.getStatus() == 0) {
                idsToBeUpdated.add(passenger.getId());
            }
        }
        
        if (!idsToBeUpdated.isEmpty()) {
            // 分批更新状态，避免单次操作数据量过大导致连接超时
            int batchSize = 5000; // 每批处理5000条记录
            for (int i = 0; i < idsToBeUpdated.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, idsToBeUpdated.size());
                List<Long> batchIds = idsToBeUpdated.subList(i, endIndex);
                int batchUpdated = highSpeedPassengerMapper.updateStatusInBatch(batchIds, 1);
                updatedCount += batchUpdated;
                
                // 记录批次处理进度
                log.info("批量更新状态进度: {}/{} 批次完成", endIndex / batchSize + 1, 
                         (int) Math.ceil((double) idsToBeUpdated.size() / batchSize));
            }
        }
        
        log.info("已更新 {} 条原始数据的状态为已清洗", updatedCount);
    }
    
    /**
     * 生成版本号
     * 规则：v{版本号(固定值,写在配置文件里面,用@Value来传入)}+{数据源}+{日期}+{文件名}
     * 示例：v1.1_csv_20240101_高铁客运量表
     * 
     * @param filename 文件名
     * @return 版本号
     */
    private String generateVersion(String filename) {
        // 数据源默认为csv（可根据实际情况扩展）
        String dataSource = "csv";
        
        // 获取当前日期
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        
        // 移除文件扩展名
        if (filename.contains(".")) {
            filename = filename.substring(0, filename.lastIndexOf("."));
        }
        
        return "v" + fixedVersion + "_" + dataSource + "_" + date + "_" + filename;
    }
    
    /**
     * 处理数据清洗逻辑
     * 根据方案四：混合策略
     * 1. 优先4合1处理：对于能明确构成完整行程的4条记录，按现有逻辑处理
     * 2. 剩余记录处理：对于剩余的1-3条记录，根据其包含的信息类型（上车/下车/其他）生成清洗记录
     * 3. 保留所有原始数据，不丢弃任何记录
     * 
     * 注意：
     * - 在4合1处理中，系统查找boardingCount > 0的记录作为上车站点，alightingCount > 0的记录作为下车站点
     * - 在部分批次处理中，所有记录都会被转换，不管其boardingCount或alightingCount是否为0
     * - 这样确保了所有原始数据都被处理，避免数据丢失
     * 
     * @param rawDataList 原始数据列表
     * @param version 版本号
     * @return 清洗后数据列表和不完整批次数量
     */
    private ProcessResult processDataCleaning(List<HighSpeedPassenger> rawDataList, String version) {
        List<HighSpeedPassengerClean> cleanDataList = new ArrayList<>();
        int incompleteBatches = 0; // 记录不完整批次数量
        
        // 按列车编码和运行日期分组
        Map<String, List<HighSpeedPassenger>> groupedData = new HashMap<>();
        for (HighSpeedPassenger passenger : rawDataList) {
            String key = passenger.getTrainCode() + "_" + passenger.getOperationDate();
            groupedData.computeIfAbsent(key, k -> new ArrayList<>()).add(passenger);
        }
        
        // 处理每个分组的数据
        for (Map.Entry<String, List<HighSpeedPassenger>> entry : groupedData.entrySet()) {
            List<HighSpeedPassenger> group = entry.getValue();
            
            // 按照4条记录为一组进行处理
            int i = 0;
            while (i < group.size()) {
                int endIndex = Math.min(i + 4, group.size());
                List<HighSpeedPassenger> batch = group.subList(i, endIndex);
                
                if (batch.size() == 4) {
                    // 优先4合1处理：对于完整的4条记录
                    HighSpeedPassengerClean cleanPassenger = convertToCleanEntity(batch, version);
                    cleanDataList.add(cleanPassenger);
                } else {
                    // 剩余记录处理：对于1-3条的不完整批次
                    List<HighSpeedPassengerClean> partialCleanData = processPartialBatch(batch, version);
                    cleanDataList.addAll(partialCleanData);
                    incompleteBatches++; // 记录不完整批次
                    log.debug("处理不完整批次，包含 {} 条记录", batch.size());
                }
                
                i += 4; // 移动到下一组
            }
        }
        
        ProcessResult result = new ProcessResult();
        result.setCleanDataList(cleanDataList);
        result.setIncompleteBatches(incompleteBatches);
        
        return result;
    }
    
    /**
     * 处理部分批次（1-3条记录）
     * 根据记录中的信息类型（上车/下车/其他）生成清洗记录
     * 
     * @param batch 部分批次记录（1-3条）
     * @param version 版本号
     * @return 清洗后的记录列表
     */
    private List<HighSpeedPassengerClean> processPartialBatch(List<HighSpeedPassenger> batch, String version) {
        List<HighSpeedPassengerClean> cleanDataList = new ArrayList<>();
        
        // 对于部分批次，每条记录都可能生成一条清洗记录
        for (HighSpeedPassenger record : batch) {
            HighSpeedPassengerClean cleanPassenger = convertPartialRecordToCleanEntity(record, version);
            if (cleanPassenger != null) {
                cleanDataList.add(cleanPassenger);
            }
        }
        
        return cleanDataList;
    }
    
    /**
     * 将单条原始记录转换为清洗后的记录（用于部分批次）
     * 
     * @param record 单条原始记录
     * @param version 版本号
     * @return 清洗后的记录
     */
    private HighSpeedPassengerClean convertPartialRecordToCleanEntity(HighSpeedPassenger record, String version) {
        HighSpeedPassengerClean cleanPassenger = new HighSpeedPassengerClean();
        
        // 生成ticketId: 列车编码_运行日期_原始ID
        if (record.getOperationDate() == null) {
            log.error("数据记录中运行日期为空，无法生成ticketId，记录ID: {}", record.getId());
            throw new IllegalArgumentException("数据记录中运行日期为空，文件格式错误");
        }
        String ticketId = record.getTrainCode() + "_" + record.getOperationDate().toString().replace("-", "") + "_" + record.getId();
        cleanPassenger.setTicketId(ticketId);
        
        // 直接复制的字段
        cleanPassenger.setOperationLineCode(record.getLineCode());
        cleanPassenger.setTrainCode(record.getTrainCode());
        cleanPassenger.setLineSiteId(record.getLineSiteId());
        cleanPassenger.setUplineCode(record.getUplineCode());
        cleanPassenger.setTravelDate(record.getOperationDate());
        cleanPassenger.setDistanceSeq(record.getDistanceOrder());
        cleanPassenger.setTicketPrice(record.getTicketPrice());
        cleanPassenger.setTicketType(record.getTicketType());
        cleanPassenger.setSeatTypeCode(record.getSeatTypeCode());
        cleanPassenger.setTrainCompanyCode(record.getTrainCompanyCode());
        cleanPassenger.setStartStationTelecode(record.getStartStationTelecode());
        cleanPassenger.setOriginStation(record.getStartStation());
        cleanPassenger.setEndStationTelecode(record.getEndStationTelecode());
        cleanPassenger.setDestStation(record.getEndStation());
        cleanPassenger.setTrainLevelCode(record.getTrainLevelCode());
        cleanPassenger.setTrainTypeCode(record.getTrainTypeCode());
        cleanPassenger.setTicketStation(record.getTicketStation());
        cleanPassenger.setFarthestArrivalStation(record.getFarthestArrivalStation());
        cleanPassenger.setTicketTime(record.getTicketTime());
        cleanPassenger.setArrivalStation(record.getArrivalStation());
        cleanPassenger.setDataVersion(version);
        cleanPassenger.setOriginalSiteId(record.getSiteId());
        
        // 解析运行时间为标准时间格式
        if (record.getOperationTime() != null && !record.getOperationTime().isEmpty()) {
            try {
                int timeValue = Integer.parseInt(record.getOperationTime());
                int hour = timeValue / 100;
                int minute = timeValue % 100;
                cleanPassenger.setDepartTime(LocalTime.of(hour, minute));
            } catch (NumberFormatException e) {
                log.warn("无法解析运行时间: {}", record.getOperationTime());
            }
        }
        
        // 设置站点信息，基于当前记录
        cleanPassenger.setDepartStationId(record.getSiteId());
        cleanPassenger.setArriveStationId(record.getSiteId());
        cleanPassenger.setBoardingCount(record.getBoardingCount());
        cleanPassenger.setAlightingCount(record.getAlightingCount());
        
        // 设置默认字段值以确保数据完整性
        cleanPassenger.setCreateTime(java.time.LocalDateTime.now());  // 创建时间为当前时间
        cleanPassenger.setUpdateTime(java.time.LocalDateTime.now());  // 更新时间为当前时间
        cleanPassenger.setStatus(1);                                  // 状态默认为1（有效）
        cleanPassenger.setIsDeleted(0);                               // 是否删除默认为0（未删除）
        
        return cleanPassenger;
    }
    
    /**
     * 将4条原始记录转换为1条清洗后的记录
     * 
     * @param batch 4条原始记录
     * @param version 版本号
     * @return 清洗后的记录
     */
    private HighSpeedPassengerClean convertToCleanEntity(List<HighSpeedPassenger> batch, String version) {
        HighSpeedPassengerClean cleanPassenger = new HighSpeedPassengerClean();
        
        // 获取第一条记录作为基础数据
        HighSpeedPassenger firstRecord = batch.get(0);
        
        // 生成ticketId: 列车编码_运行日期_归一化序列
        long normalizedSequence = firstRecord.getId() / 4;
        if (firstRecord.getOperationDate() == null) {
            log.error("数据记录中运行日期为空，无法生成ticketId，记录ID: {}", firstRecord.getId());
            throw new IllegalArgumentException("数据记录中运行日期为空，文件格式错误");
        }
        String ticketId = firstRecord.getTrainCode() + "_" + firstRecord.getOperationDate().toString().replace("-", "") + "_" + normalizedSequence;
        cleanPassenger.setTicketId(ticketId);
        
        // 直接复制的字段
        cleanPassenger.setOperationLineCode(firstRecord.getLineCode());
        cleanPassenger.setTrainCode(firstRecord.getTrainCode());
        cleanPassenger.setLineSiteId(firstRecord.getLineSiteId());
        cleanPassenger.setUplineCode(firstRecord.getUplineCode());
        cleanPassenger.setTravelDate(firstRecord.getOperationDate());
        cleanPassenger.setDistanceSeq(firstRecord.getDistanceOrder());
        cleanPassenger.setTicketPrice(firstRecord.getTicketPrice());
        cleanPassenger.setTicketType(firstRecord.getTicketType());
        cleanPassenger.setSeatTypeCode(firstRecord.getSeatTypeCode());
        cleanPassenger.setTrainCompanyCode(firstRecord.getTrainCompanyCode());
        cleanPassenger.setStartStationTelecode(firstRecord.getStartStationTelecode());
        cleanPassenger.setOriginStation(firstRecord.getStartStation());
        cleanPassenger.setEndStationTelecode(firstRecord.getEndStationTelecode());
        cleanPassenger.setDestStation(firstRecord.getEndStation());
        cleanPassenger.setTrainLevelCode(firstRecord.getTrainLevelCode());
        cleanPassenger.setTrainTypeCode(firstRecord.getTrainTypeCode());
        cleanPassenger.setTicketStation(firstRecord.getTicketStation());
        cleanPassenger.setFarthestArrivalStation(firstRecord.getFarthestArrivalStation());
        cleanPassenger.setTicketTime(firstRecord.getTicketTime());
        cleanPassenger.setArrivalStation(firstRecord.getArrivalStation());
        cleanPassenger.setDataVersion(version);
        
        // 解析运行时间为标准时间格式
        if (firstRecord.getOperationTime() != null && !firstRecord.getOperationTime().isEmpty()) {
            try {
                int timeValue = Integer.parseInt(firstRecord.getOperationTime());
                int hour = timeValue / 100;
                int minute = timeValue % 100;
                cleanPassenger.setDepartTime(LocalTime.of(hour, minute));
            } catch (NumberFormatException e) {
                log.warn("无法解析运行时间: {}", firstRecord.getOperationTime());
            }
        }
        
        // 查找上车站点和下车站点
        HighSpeedPassenger boardingRecord = null;  // 上客记录
        HighSpeedPassenger alightingRecord = null; // 下客记录
        
        for (HighSpeedPassenger record : batch) {
            if (record.getBoardingCount() != null && record.getBoardingCount() > 0) {
                boardingRecord = record;
            }
            if (record.getAlightingCount() != null && record.getAlightingCount() > 0) {
                alightingRecord = record;
            }
        }
        
        if (boardingRecord != null) {
            cleanPassenger.setDepartStationId(boardingRecord.getSiteId());
            cleanPassenger.setBoardingCount(boardingRecord.getBoardingCount());
            cleanPassenger.setOriginalSiteId(boardingRecord.getSiteId());
        }
        
        if (alightingRecord != null) {
            cleanPassenger.setArriveStationId(alightingRecord.getSiteId());
            cleanPassenger.setAlightingCount(alightingRecord.getAlightingCount());
        }
        
        // 设置默认字段值以确保数据完整性
        cleanPassenger.setCreateTime(java.time.LocalDateTime.now());  // 创建时间为当前时间
        cleanPassenger.setUpdateTime(java.time.LocalDateTime.now());  // 更新时间为当前时间
        cleanPassenger.setStatus(1);                                  // 状态默认为1（有效）
        cleanPassenger.setIsDeleted(0);                               // 是否删除默认为0（未删除）
        
        return cleanPassenger;
    }
    
    private int batchInsertCleanPassengers(List<HighSpeedPassengerClean> passengers) {
        int batchSize = 1000; // 减小批量大小以减少内存使用
        int totalInserted = 0;
        int totalBatches = (int) Math.ceil((double) passengers.size() / batchSize);
        
        log.info("开始批量插入清洗数据 {} 条，分为 {} 个批次", passengers.size(), totalBatches);

        for (int i = 0; i < passengers.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, passengers.size());
            List<HighSpeedPassengerClean> batch = passengers.subList(i, endIndex);
            try {
                int insertedCount = highSpeedPassengerCleanMapper.insertBatchSomeColumn(batch);
                totalInserted += insertedCount;
                
                // 每完成10个批次输出一次进度
                int currentBatch = (i / batchSize) + 1;
                if (currentBatch % 10 == 0 || currentBatch == totalBatches) {
                    log.info("清洗数据批量插入进度: {}/{} 批次完成", currentBatch, totalBatches);
                }
            } catch (Exception e) {
                log.warn("清洗数据批次插入失败，尝试单条插入");
                // 失败时尝试单条插入
                totalInserted += retrySingleCleanInsert(batch);
            }
        }
        
        log.info("清洗数据批量插入完成，成功插入: {} 条", totalInserted);
        return totalInserted;
    }
    
    private int retrySingleCleanInsert(List<HighSpeedPassengerClean> batch) {
        int successCount = 0;
        for (HighSpeedPassengerClean passenger : batch) {
            try {
                highSpeedPassengerCleanMapper.insert(passenger);
                successCount++;
            } catch (Exception e) {
                // 避免输出过多日志，仅记录错误数量
            }
        }
        return successCount;
    }

    private int batchInsertPassengers(List<HighSpeedPassenger> passengers) {
        int batchSize = 1000; // 减小批量大小以减少内存使用
        int totalInserted = 0;
        int totalBatches = (int) Math.ceil((double) passengers.size() / batchSize);
        
        log.info("开始批量插入 {} 条记录，分为 {} 个批次", passengers.size(), totalBatches);

        for (int i = 0; i < passengers.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, passengers.size());
            List<HighSpeedPassenger> batch = passengers.subList(i, endIndex);
            try {
                int insertedCount = highSpeedPassengerMapper.insertBatchSomeColumn(batch);
                totalInserted += insertedCount;
                
                // 每完成10个批次输出一次进度
                int currentBatch = (i / batchSize) + 1;
                if (currentBatch % 10 == 0 || currentBatch == totalBatches) {
                    log.info("批量插入进度: {}/{} 批次完成", currentBatch, totalBatches);
                }
            } catch (Exception e) {
                log.warn("批次插入失败，尝试单条插入");
                // 失败时尝试单条插入
                totalInserted += retrySingleInsert(batch);
            }
        }
        
        log.info("批量插入完成，成功插入: {} 条", totalInserted);
        return totalInserted;
    }
    
    private int retrySingleInsert(List<HighSpeedPassenger> batch) {
        int successCount = 0;
        for (HighSpeedPassenger passenger : batch) {
            try {
                highSpeedPassengerMapper.insert(passenger);
                successCount++;
            } catch (Exception e) {
                // 避免输出过多日志，仅记录错误数量
            }
        }
        return successCount;
    }

    private List<Map<String, String>> parseCsvFile(MultipartFile file) throws IOException {
        List<Map<String, String>> result = new ArrayList<>();
        // 自动检测文件编码
        Charset charset = detectCharset(file);
        log.info("检测到文件编码: {}", charset.name());
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;
            String[] headers = null;
            
            // 仅记录前几行和进度
            int totalLines = 0;
            String[] lines = file.getInputStream().readAllBytes().toString().split("\n");
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // 第1行：跳过（无关信息）
                if (lineNumber == 1) {
                    continue; // 不再记录跳过信息
                }
                // 第2行：解析为列名/表头
                if (lineNumber == 2) {
                    headers = parseCsvLine(line);
                    log.info("解析到表头，共 {} 列", headers.length);
                    continue;
                }
                // 第3行及以后：解析为数据
                if (headers == null) {
                    throw new IOException("未找到表头行");
                }

                // 使用更健壮的CSV解析
                String[] values = parseCsvLine(line);
                if (values.length < headers.length) {
                    // 不再记录每行的字段不足警告，避免日志过多
                }

                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    String value = (i < values.length) ? values[i] : "";
                    // 处理 NULL 值和空值
                    if ("NULL".equalsIgnoreCase(value) || value == null || value.trim().isEmpty()) {
                        value = "";
                    }
                    row.put(headers[i], value.trim());
                }
                result.add(row);

                // 仅记录前几行数据用于调试
                if (lineNumber <= 3) {
                    log.debug("第{}行数据示例: {}", lineNumber, row);
                }
                
                // 每处理10000行记录一次进度
                if (lineNumber % 10000 == 0) {
                    log.info("CSV解析进度: {} 行已处理", lineNumber);
                }
            }

            log.info("共解析 {} 行数据", result.size());
        }
        return result;
    }


    /**
     * 通用的CSV行解析方法，处理制表符或逗号分隔
     */
    private String[] parseCsvLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return new String[0];
        }

        // 先尝试制表符分割（根据您之前的数据示例）
        if (line.contains("\t")) {
            return line.split("\t", -1); // 使用-1保留空字段
        }
        // 如果没有制表符，使用逗号分割
        else {
            return parseCsvWithCommas(line);
        }
    }

    /**
     * 处理逗号分隔的CSV（考虑引号内的逗号）
     */
    private String[] parseCsvWithCommas(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        result.add(field.toString().trim());

        return result.toArray(new String[0]);
    }

    private HighSpeedPassenger convertToEntity(Map<String, String> row) {
        HighSpeedPassenger passenger = new HighSpeedPassenger();

        // 使用安全的转换方法
        passenger.setId(safeParseLong(row.get("序号")));
        passenger.setLineCode(safeGetString(row.get("运营线路编码")));
        passenger.setTrainCode(safeParseInteger(row.get("列车编码")));
        passenger.setSiteId(safeParseInteger(row.get("站点id")));
        passenger.setLineSiteId(safeParseInteger(row.get("线路站点id")));
        passenger.setUplineCode(safeGetString(row.get("上行线编码")));

        // 处理日期格式
        passenger.setOperationDate(safeParseDate(row.get("运行日期")));
        passenger.setOperationTime(safeGetString(row.get("运行时间")));
        passenger.setDistanceOrder(safeParseInteger(row.get("与起点站距序")));
        passenger.setIsStartSite(safeParseInteger(row.get("是否起始站点")));
        passenger.setIsEndSite(safeParseInteger(row.get("是否终点站点")));
        passenger.setArrivalTime(safeGetString(row.get("到达时间")));
        passenger.setDepartureTime(safeGetString(row.get("出发时间")));
        passenger.setStopTime(safeGetString(row.get("经停时间")));
        passenger.setPassengerFlow(safeParseInteger(row.get("客流量")));
        passenger.setUplinePassengerFlow(safeParseInteger(row.get("上行客流量")));
        passenger.setDownlinePassengerFlow(safeParseInteger(row.get("下行客流量")));
        passenger.setBoardingCount(safeParseInteger(row.get("上客量")));
        passenger.setAlightingCount(safeParseInteger(row.get("下客量")));
        passenger.setRemarks(safeGetString(row.get("备注")));
        passenger.setTrainDepartureDate(safeParseDate(row.get("列车出发日期")));

        // 注意：根据您的数据示例，可能是"列出出发时间"而不是"列车出发时间"
        String trainDepartureTime = row.get("列车出发时间");
        if (trainDepartureTime == null || trainDepartureTime.isEmpty()) {
            trainDepartureTime = row.get("列出出发时间"); // 备用字段名
        }
        passenger.setTrainDepartureTime(safeGetString(trainDepartureTime));

        passenger.setSiteSequence(safeParseInteger(row.get("站点序号")));
        passenger.setTicketType(safeParseInteger(row.get("车票类型")));
        passenger.setTicketPrice(safeParseBigDecimal(row.get("车票价格")));
        passenger.setSeatTypeCode(safeGetString(row.get("座位类型编码")));
        passenger.setTrainCompanyCode(safeGetString(row.get("列车公司编码")));
        passenger.setStartDate(safeParseDate(row.get("开始日期")));
        passenger.setStartStationTelecode(safeGetString(row.get("起点站电报码")));
        passenger.setStartStation(safeGetString(row.get("起点站")));
        passenger.setEndStationTelecode(safeGetString(row.get("终到站电报码")));
        passenger.setEndStation(safeGetString(row.get("终到站")));
        passenger.setTrainLevelCode(safeGetString(row.get("列车等级码")));
        passenger.setTrainTypeCode(safeGetString(row.get("列车类型码")));
        passenger.setTicketStation(safeGetString(row.get("售票站")));
        passenger.setFarthestArrivalStation(safeGetString(row.get("最远到达站")));
        passenger.setTicketTime(safeParseDate(row.get("售票时间")));
        passenger.setArrivalStation(safeGetString(row.get("到达站")));
        passenger.setRevenue(safeParseBigDecimal(row.get("收入")));

        return passenger;
    }
    // 安全的类型转换方法
    private String safeGetString(String value) {
        return value == null ? "" : value.trim();
    }

    private Integer safeParseInteger(String value) {
        if (value == null || value.trim().isEmpty() || "NULL".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            // 移除可能的非数字字符
            String cleanedValue = value.trim().replaceAll("[^0-9-]", "");
            return cleanedValue.isEmpty() ? null : Integer.parseInt(cleanedValue);
        } catch (NumberFormatException e) {
            log.warn("整数解析失败: {}", value);
            return null;
        }
    }

    private Long safeParseLong(String value) {
        if (value == null || value.trim().isEmpty() || "NULL".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            String cleanedValue = value.trim().replaceAll("[^0-9-]", "");
            return cleanedValue.isEmpty() ? null : Long.parseLong(cleanedValue);
        } catch (NumberFormatException e) {
            log.warn("长整数解析失败: {}", value);
            return null;
        }
    }

    private BigDecimal safeParseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty() || "NULL".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            String cleanedValue = value.trim().replaceAll("[^0-9.-]", "");
            return cleanedValue.isEmpty() ? null : new BigDecimal(cleanedValue);
        } catch (NumberFormatException e) {
            log.warn("金额解析失败: {}", value);
            return null;
        }
    }

    private LocalDate safeParseDate(String value) {
        if (value == null || value.trim().isEmpty() || "NULL".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            String cleanedValue = value.trim();

            // 处理数字格式日期：20150101
            if (cleanedValue.matches("\\d{8}")) {
                int year = Integer.parseInt(cleanedValue.substring(0, 4));
                int month = Integer.parseInt(cleanedValue.substring(4, 6));
                int day = Integer.parseInt(cleanedValue.substring(6, 8));
                return LocalDate.of(year, month, day);
            }

            // 处理标准日期格式
            return LocalDate.parse(cleanedValue, DATE_FORMATTER);
        } catch (Exception e) {
            log.warn("日期解析失败: {}", value);
            return null;
        }
    }
    /**
     * 自动检测文件编码
     */
    private Charset detectCharset(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();

            // 检查UTF-8 BOM
            if (bytes.length >= 3 &&
                    bytes[0] == (byte) 0xEF &&
                    bytes[1] == (byte) 0xBB &&
                    bytes[2] == (byte) 0xBF) {
                return StandardCharsets.UTF_8;
            }

            // 尝试用常见编码解析第一行
            String firstLine = new String(bytes, 0, Math.min(bytes.length, 1000));

            // 检查是否包含中文字符
            if (containsChinese(firstLine)) {
                // 尝试GBK编码
                try {
                    String gbkLine = new String(bytes, "GBK");
                    if (!containsGarbledText(gbkLine)) {
                        return Charset.forName("GBK");
                    }
                } catch (Exception e) {
                    // 忽略，继续尝试其他编码
                }

                // 尝试GB2312编码
                try {
                    String gb2312Line = new String(bytes, "GB2312");
                    if (!containsGarbledText(gb2312Line)) {
                        return Charset.forName("GB2312");
                    }
                } catch (Exception e) {
                    // 忽略
                }
            }

            // 默认使用UTF-8
            return StandardCharsets.UTF_8;

        } catch (Exception e) {
            log.warn("编码检测失败，使用默认UTF-8编码", e);
            return StandardCharsets.UTF_8;
        }
    }



    /**
     * 检查是否包含中文字符
     */
    private boolean containsChinese(String text) {
        return text.matches(".*[\\u4e00-\\u9fa5].*");
    }

    /**
     * 检查是否包含乱码
     */
    private boolean containsGarbledText(String text) {
        return text.contains("") || text.contains("ï»¿") || text.matches(".*[\\u0000-\\u001F\\u007F-\\u009F].*");
    }

    /**
     * 检查ID是否已存在于数据库中
     */
    private boolean checkIdExistsInDatabase(Long id) {
        return highSpeedPassengerMapper.selectById(id) != null;
    }
}