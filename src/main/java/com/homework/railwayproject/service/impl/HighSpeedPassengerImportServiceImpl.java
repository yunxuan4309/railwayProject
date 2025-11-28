package com.homework.railwayproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.homework.railwayproject.mapper.HighSpeedPassengerMapper;
import com.homework.railwayproject.mapper.HighSpeedPassengerCleanMapper;
import com.homework.railwayproject.pojo.dto.ImportResult;
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

        try {
            log.info("开始解析CSV文件，文件名: {}，大小: {} bytes",
                    file.getOriginalFilename(), file.getSize());

            List<Map<String, String>> csvData = parseCsvFile(file);
            log.info("成功解析 {} 行数据", csvData.size());

            // 跳过数据验证步骤，直接进行转换
            // csvDataPreprocessor.validateCsvData(csvData);

            log.info("跳过数据验证，直接进行数据转换");

            // 转换为实体对象
            int successCount = 0;
            int failCount = 0;

            for (int i = 0; i < csvData.size(); i++) {
                Map<String, String> row = csvData.get(i);
                try {
                    HighSpeedPassenger passenger = convertToEntity(row);
                    // 设置默认状态为0（未清洗）
                    passenger.setStatus(0);
                    passengerList.add(passenger);
                    successCount++;

                    // 记录前几行的转换结果用于调试
                    if (i < 3) {
                        log.debug("第{}行转换成功 - 序号: {}, 运行日期: {}",
                                i + 3, row.get("序号"), row.get("运行日期"));
                    }
                } catch (Exception e) {
                    log.warn("第{}行数据转换失败: {}", i + 3, e.getMessage());
                    failCount++;
                }
            }

            log.info("数据转换完成 - 成功: {} 行，失败: {} 行", successCount, failCount);

            // 批量插入数据库
            int insertedCount = batchInsertPassengers(passengerList);
            result.setSuccessCount(insertedCount);
            result.setFailedCount(failCount + (passengerList.size() - insertedCount));

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
    public ImportResult cleanData(String filename) {
        ImportResult result = new ImportResult();
        
        try {
            log.info("开始数据清洗，文件名: {}", filename);
            
            // 查询所有未清洗的原始数据（status = 0）
            QueryWrapper<HighSpeedPassenger> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", 0);
            List<HighSpeedPassenger> rawDataList = highSpeedPassengerMapper.selectList(queryWrapper);
            log.info("查询到 {} 条未清洗的原始数据", rawDataList.size());
            
            if (rawDataList.isEmpty()) {
                log.info("没有需要清洗的数据");
                result.setSuccessCount(0);
                result.setFailedCount(0);
                return result;
            }
            
            // 生成版本号：v{fixedVersion}_{数据源}_{日期}_{filename}
            String version = generateVersion(filename);
            
            // 按照清洗规则进行数据清洗
            List<HighSpeedPassengerClean> cleanDataList = processDataCleaning(rawDataList, version);
            log.info("清洗后得到 {} 条数据", cleanDataList.size());
            
            // 批量插入清洗后的数据
            int insertedCount = batchInsertCleanPassengers(cleanDataList);
            result.setSuccessCount(insertedCount);
            result.setFailedCount(cleanDataList.size() - insertedCount);
            
            // 更新原始数据的状态为已清洗（status = 1）
            updateRawDataStatus(rawDataList);
            
            log.info("清洗完成，成功插入: {} 条，总失败: {} 条",
                    result.getSuccessCount(), result.getFailedCount());
                    
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
        for (HighSpeedPassenger passenger : rawDataList) {
            passenger.setStatus(1);
            highSpeedPassengerMapper.updateById(passenger);
        }
        log.info("已更新 {} 条原始数据的状态为已清洗", rawDataList.size());
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
     * 根据规则：4条原始记录合并为1条清洗后记录
     * 
     * @param rawDataList 原始数据列表
     * @param version 版本号
     * @return 清洗后数据列表
     */
    private List<HighSpeedPassengerClean> processDataCleaning(List<HighSpeedPassenger> rawDataList, String version) {
        List<HighSpeedPassengerClean> cleanDataList = new ArrayList<>();
        
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
            for (int i = 0; i < group.size(); i += 4) {
                int endIndex = Math.min(i + 4, group.size());
                List<HighSpeedPassenger> batch = group.subList(i, endIndex);
                
                // 确保至少有4条记录才能处理
                if (batch.size() == 4) {
                    HighSpeedPassengerClean cleanPassenger = convertToCleanEntity(batch, version);
                    cleanDataList.add(cleanPassenger);
                } else {
                    log.warn("数据不完整，跳过处理。需要4条记录，实际只有 {} 条", batch.size());
                }
            }
        }
        
        return cleanDataList;
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
        
        return cleanPassenger;
    }
    
    private int batchInsertCleanPassengers(List<HighSpeedPassengerClean> passengers) {
        int batchSize = 2000;
        int totalInserted = 0;

        for (int i = 0; i < passengers.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, passengers.size());
            List<HighSpeedPassengerClean> batch = passengers.subList(i, endIndex);
            try {
                int insertedCount = highSpeedPassengerCleanMapper.insertBatchSomeColumn(batch);
                totalInserted += insertedCount;
            } catch (Exception e) {
                log.error("批次插入失败，尝试单条插入", e);
                // 失败时尝试单条插入
                totalInserted += retrySingleCleanInsert(batch);
            }
        }
        return totalInserted;
    }
    
    private int retrySingleCleanInsert(List<HighSpeedPassengerClean> batch) {
        int successCount = 0;
        for (HighSpeedPassengerClean passenger : batch) {
            try {
                highSpeedPassengerCleanMapper.insert(passenger);
                successCount++;
            } catch (Exception e) {
                log.warn("单条插入失败: {}", passenger.getTicketId());
            }
        }
        return successCount;
    }

    private int batchInsertPassengers(List<HighSpeedPassenger> passengers) {
        int batchSize = 2000;
        int totalInserted = 0;

        for (int i = 0; i < passengers.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, passengers.size());
            List<HighSpeedPassenger> batch = passengers.subList(i, endIndex);
            try {
                int insertedCount = highSpeedPassengerMapper.insertBatchSomeColumn(batch);
                totalInserted += insertedCount;
            } catch (Exception e) {
                log.error("批次插入失败，尝试单条插入", e);
                // 失败时尝试单条插入
                totalInserted += retrySingleInsert(batch);
            }
        }
        return totalInserted;
    }

    private int retrySingleInsert(List<HighSpeedPassenger> batch) {
        int successCount = 0;
        for (HighSpeedPassenger passenger : batch) {
            try {
                highSpeedPassengerMapper.insert(passenger);
                successCount++;
            } catch (Exception e) {
                log.warn("单条插入失败: {}", passenger.getId());
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
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // 第1行：跳过（无关信息）
                if (lineNumber == 1) {
                    log.info("跳过第1行（文件说明）: {}", line);
                    continue;
                }
                // 第2行：解析为列名/表头
                if (lineNumber == 2) {
                    headers = parseCsvLine(line);
                    log.info("解析到表头，共 {} 列: {}", headers.length, Arrays.toString(headers));
                    continue;
                }
                // 第3行及以后：解析为数据
                if (headers == null) {
                    throw new IOException("未找到表头行");
                }


                // 使用更健壮的CSV解析
                String[] values = parseCsvLine(line);
                if (values.length < headers.length) {
                    log.warn("第{}行数据字段数量不足，期望: {}, 实际: {}", lineNumber, headers.length, values.length);
                    // 可以继续处理，用空值填充缺失的字段
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

                // 记录前几行数据用于调试
                if (lineNumber <= 5) {
                    log.debug("第{}行数据示例: {}", lineNumber, row);
                }
            }

            log.info("共解析 {} 行数据（从第3行到第{}行）", result.size(), lineNumber);
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

}