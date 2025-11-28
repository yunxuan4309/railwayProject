package com.homework.railwayproject.util.csvUtil;

import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.web.ServiceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CsvDataPreprocessor {

    private static final DateTimeFormatter STANDARD_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter NUMBER_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 宽松的数据验证 - 支持数字格式日期
     */
    public void validateCsvData(List<Map<String, String>> csvData) {
        if (csvData == null || csvData.isEmpty()) {
            throw new ServiceException(ServiceCode.ERROR_BAD_REQUEST, "CSV数据为空");
        }

        for (int i = 0; i < csvData.size(); i++) {
            Map<String, String> row = csvData.get(i);
            int rowNumber = i + 3; // +3 因为跳过了前2行（第1行说明，第2行表头）

            try {
                validateRequiredFields(row, rowNumber);
                validateDataTypes(row, rowNumber);
            } catch (ServiceException e) {
                log.warn("第{}行数据验证失败: {}", rowNumber, e.getMessage());
                throw e;
            }
        }
    }

    /**
     * 验证必填字段
     */
    private void validateRequiredFields(Map<String, String> row, int rowNumber) {
        // 根据实际数据调整必填字段
        String[] requiredFields = {
                "序号", "运营线路编码", "列车编码", "站点id", "运行日期",
                "运行时间", "车票类型", "车票价格", "收入"
        };

        for (String field : requiredFields) {
            String value = row.get(field);
            if (value == null || value.trim().isEmpty() || "NULL".equalsIgnoreCase(value)) {
                throw new ServiceException(ServiceCode.ERROR_BAD_REQUEST,
                        String.format("第%d行: 必填字段 '%s' 为空", rowNumber, field));
            }
        }
    }

    /**
     * 宽松的数据类型验证 - 支持数字格式日期
     */
    private void validateDataTypes(Map<String, String> row, int rowNumber) {
        // 验证数字字段
        validateIntegerField(row, "序号", rowNumber, false);
        validateIntegerField(row, "列车编码", rowNumber, false);
        validateIntegerField(row, "站点id", rowNumber, false);
        validateIntegerField(row, "车票类型", rowNumber, false);

        // 验证日期字段 - 支持数字格式
        validateDateFieldFlexible(row, "运行日期", rowNumber, false);
        validateDateFieldFlexible(row, "列车出发日期", rowNumber, true);
        validateDateFieldFlexible(row, "开始日期", rowNumber, true);
        validateDateFieldFlexible(row, "售票时间", rowNumber, true);

        // 验证金额字段
        validateBigDecimalField(row, "车票价格", rowNumber, false);
        validateBigDecimalField(row, "收入", rowNumber, false);

        // 验证时间格式
        validateTimeField(row, "运行时间", rowNumber, false);
        validateTimeField(row, "到达时间", rowNumber, true);
        validateTimeField(row, "出发时间", rowNumber, true);
        validateTimeField(row, "列车出发时间", rowNumber, true);
    }

    /**
     * 灵活的日期验证 - 支持数字格式 (20150101) 和标准格式 (2015-01-01)
     */
    private void validateDateFieldFlexible(Map<String, String> row, String fieldName, int rowNumber, boolean allowEmpty) {
        String value = row.get(fieldName);
        if (value == null || value.trim().isEmpty() || "NULL".equalsIgnoreCase(value)) {
            if (!allowEmpty) {
                throw new ServiceException(ServiceCode.ERROR_BAD_REQUEST,
                        String.format("第%d行: 字段 '%s' 不能为空", rowNumber, fieldName));
            }
            return;
        }

        String cleanedValue = value.trim();
        boolean valid = false;
        String detectedFormat = "";

        // 检查是否是8位数字格式：20150101
        if (cleanedValue.matches("\\d{8}")) {
            try {
                int year = Integer.parseInt(cleanedValue.substring(0, 4));
                int month = Integer.parseInt(cleanedValue.substring(4, 6));
                int day = Integer.parseInt(cleanedValue.substring(6, 8));
                // 验证日期是否有效
                LocalDate.of(year, month, day);
                valid = true;
                detectedFormat = "yyyyMMdd";
            } catch (Exception e) {
                valid = false;
            }
        }
        // 检查标准日期格式：2015-01-01
        else if (cleanedValue.matches("\\d{4}-\\d{2}-\\d{2}")) {
            try {
                LocalDate.parse(cleanedValue, STANDARD_DATE_FORMATTER);
                valid = true;
                detectedFormat = "yyyy-MM-dd";
            } catch (DateTimeParseException e) {
                valid = false;
            }
        }
        // 检查其他可能的日期格式
        else {
            // 可以在这里添加其他日期格式的支持
            valid = false;
        }

        if (!valid) {
            throw new ServiceException(ServiceCode.ERROR_BAD_REQUEST,
                    String.format("第%d行: 字段 '%s' 的值 '%s' 不是有效的日期格式 (支持: yyyy-MM-dd 或 yyyyMMdd)",
                            rowNumber, fieldName, value));
        } else {
            log.debug("第{}行: 字段 '{}' 值 '{}' 格式为 {}", rowNumber, fieldName, value, detectedFormat);
        }
    }

    private void validateIntegerField(Map<String, String> row, String fieldName, int rowNumber, boolean allowEmpty) {
        String value = row.get(fieldName);
        if (value == null || value.trim().isEmpty() || "NULL".equalsIgnoreCase(value)) {
            if (!allowEmpty) {
                throw new ServiceException(ServiceCode.ERROR_BAD_REQUEST,
                        String.format("第%d行: 字段 '%s' 不能为空", rowNumber, fieldName));
            }
            return;
        }

        try {
            // 移除可能的空格和特殊字符
            String cleanedValue = value.trim().replaceAll("[^0-9.-]", "");
            if (!cleanedValue.isEmpty()) {
                Integer.parseInt(cleanedValue);
            }
        } catch (NumberFormatException e) {
            throw new ServiceException(ServiceCode.ERROR_BAD_REQUEST,
                    String.format("第%d行: 字段 '%s' 的值 '%s' 不是有效的整数", rowNumber, fieldName, value));
        }
    }

    private void validateBigDecimalField(Map<String, String> row, String fieldName, int rowNumber, boolean allowEmpty) {
        String value = row.get(fieldName);
        if (value == null || value.trim().isEmpty() || "NULL".equalsIgnoreCase(value)) {
            if (!allowEmpty) {
                throw new ServiceException(ServiceCode.ERROR_BAD_REQUEST,
                        String.format("第%d行: 字段 '%s' 不能为空", rowNumber, fieldName));
            }
            return;
        }

        try {
            // 移除可能的货币符号和空格
            String cleanedValue = value.trim().replaceAll("[^0-9.-]", "");
            if (!cleanedValue.isEmpty()) {
                Double.parseDouble(cleanedValue); // 先用Double验证，BigDecimal更严格
            }
        } catch (NumberFormatException e) {
            throw new ServiceException(ServiceCode.ERROR_BAD_REQUEST,
                    String.format("第%d行: 字段 '%s' 的值 '%s' 不是有效的数字", rowNumber, fieldName, value));
        }
    }

    private void validateTimeField(Map<String, String> row, String fieldName, int rowNumber, boolean allowEmpty) {
        String value = row.get(fieldName);
        if (value == null || value.trim().isEmpty() || "NULL".equalsIgnoreCase(value)) {
            if (!allowEmpty) {
                throw new ServiceException(ServiceCode.ERROR_BAD_REQUEST,
                        String.format("第%d行: 字段 '%s' 不能为空", rowNumber, fieldName));
            }
            return;
        }

        try {
            validateTimeFormat(value);
        } catch (IllegalArgumentException e) {
            throw new ServiceException(ServiceCode.ERROR_BAD_REQUEST,
                    String.format("第%d行: 字段 '%s' 的值 '%s' 不是有效的时间格式 (HHmm)", rowNumber, fieldName, value));
        }
    }

    // 原有的时间验证方法
    private void validateTimeFormat(String time) {
        if (time == null || time.trim().isEmpty()) {
            throw new IllegalArgumentException("时间不能为空");
        }

        String cleanedTime = time.trim();

        // 支持4位数字格式：0749
        if (cleanedTime.matches("\\d{4}")) {
            try {
                int hour = Integer.parseInt(cleanedTime.substring(0, 2));
                int minute = Integer.parseInt(cleanedTime.substring(2, 4));
                if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                    throw new IllegalArgumentException("时间值超出有效范围");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("时间格式不正确");
            }
        }
        // 支持带冒号的格式：07:49
        else if (cleanedTime.matches("\\d{1,2}:\\d{2}")) {
            try {
                String[] parts = cleanedTime.split(":");
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                    throw new IllegalArgumentException("时间值超出有效范围");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("时间格式不正确");
            }
        }
        else {
            throw new IllegalArgumentException("时间格式不正确，支持格式: HHmm 或 HH:mm");
        }
    }
}