package com.homework.railwayproject.controller;


import com.homework.railwayproject.exception.ServiceException;
import com.homework.railwayproject.pojo.dto.CleaningResult;
import com.homework.railwayproject.pojo.dto.ImportProgress;
import com.homework.railwayproject.pojo.dto.ImportResult;
import com.homework.railwayproject.service.HighSpeedPassengerImportService;
import com.homework.railwayproject.web.JsonResult;
import com.homework.railwayproject.web.ServiceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 17214
 */ //Author:[谢云轩]
//QQ:[1721476339]
//ID:[632307060623]
//Date:2025/11/26
//Time:8:49
@Slf4j
@RestController
@RequestMapping("/api/import")
public class ImportController {

    @Autowired
    private HighSpeedPassengerImportService importService;

    /**
     * 导入CSV数据
     */
    /**
     * 导入高铁客运CSV数据
     * 
     * 该接口用于导入高铁客运原始数据CSV文件，支持重复数据检测和标记功能
     * 
     * 功能说明：
     * 1. 解析上传的CSV文件
     * 2. 检测原始数据中的重复ID
     * 3. 重复数据将被插入但状态标记为3（重复）
     * 4. 非重复数据状态标记为0（未清洗）
     * 5. 导入完成后自动触发数据清洗流程
     * 
     * @param file 上传的CSV文件，应包含高铁客运原始数据
     * @return 导入结果，包含成功、失败的记录数统计
     * 
     * 请求示例：
     * POST /api/import/high-speed-passenger
     * Content-Type: multipart/form-data
     * Body: file=file.csv
     * 
     * 响应示例：
     * {
     *   "code": 200,
     *   "data": {
     *     "successCount": 1000,
     *     "failedCount": 5
     *   },
     *   "message": "success"
     * }
     */
    @PostMapping("/high-speed-passenger")
    public JsonResult<ImportResult> importHighSpeedPassenger(@RequestParam("file") MultipartFile file) {
        try {
            ImportResult result = importService.importCsvData(file);
            // 导入完成后自动执行数据清洗
            importService.cleanData(file.getOriginalFilename());
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("导入失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, "导入失败"));
        }
    }
    
    /**
     * 清洗数据
     */
    /**
     * 手动触发数据清洗
     * 
     * 该接口用于手动执行数据清洗流程，将原始数据转换为清洗后的数据
     * 
     * 清洗策略：
     * 1. 跳过状态为3的重复数据
     * 2. 对于完整的4条记录组，执行4合1合并策略
     * 3. 对于剩余的1-3条记录，单独转换为清洗记录
     * 4. 生成清洗后的数据版本号
     * 
     * @param filename 原始数据文件名，用于生成数据版本号
     * @return 清洗结果统计，包含处理总数、成功数、失败数、跳过数和不完整批次数
     * 
     * 请求示例：
     * POST /api/import/high-speed-passenger/clean
     * Content-Type: application/x-www-form-urlencoded
     * Body: filename=test.csv
     * 
     * 响应示例：
     * {
     *   "code": 200,
     *   "data": {
     *     "totalProcessed": 2000,
     *     "successCount": 1995,
     *     "failedCount": 2,
     *     "skippedCount": 3,
     *     "incompleteBatches": 5,
     *     "message": "清洗完成..."
     *   },
     *   "message": "success"
     * }
     */
    @PostMapping("/high-speed-passenger/clean")
    public JsonResult<CleaningResult> cleanHighSpeedPassenger(@RequestParam("filename") String filename) {
        try {
            CleaningResult result = importService.cleanData(filename);
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("清洗失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, "清洗失败"));
        }
    }
    
    /**
     * 异步导入高铁客运CSV数据
     * 
     * 该接口用于异步导入高铁客运原始数据CSV文件，支持进度跟踪功能
     * 
     * 功能说明：
     * 1. 启动异步导入任务
     * 2. 返回任务ID供前端查询进度
     * 3. 导入完成后自动触发数据清洗
     * 
     * @param file 上传的CSV文件，应包含高铁客运原始数据
     * @return 任务ID，用于查询导入进度
     * 
     * 请求示例：
     * POST /api/import/high-speed-passenger/async
     * Content-Type: multipart/form-data
     * Body: file=file.csv
     * 
     * 响应示例：
     * {
     *   "code": 200,
     *   "data": "task-uuid-string",
     *   "message": "success"
     * }
     */
    @PostMapping("/high-speed-passenger/async")
    public JsonResult<String> importHighSpeedPassengerAsync(@RequestParam("file") MultipartFile file) {
        try {
            String taskId = importService.importCsvDataAsync(file);
            return JsonResult.ok(taskId);
        } catch (Exception e) {
            log.error("异步导入启动失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, "异步导入启动失败"));
        }
    }
    
    /**
     * 查询导入任务进度
     * 
     * 该接口用于查询异步导入任务的进度状态
     * 
     * @param taskId 任务ID
     * @return 导入进度信息，包含状态、进度百分比、处理统计等
     * 
     * 请求示例：
     * GET /api/import/progress?taskId=task-uuid
     * 
     * 响应示例：
     * {
     *   "code": 200,
     *   "data": {
     *     "taskId": "task-uuid",
     *     "status": "RUNNING",
     *     "totalRecords": 10000,
     *     "processedRecords": 5000,
     *     "progressPercentage": 50.0,
     *     "successCount": 4950,
     *     "failedCount": 50,
     *     "message": "正在处理数据，进度: 50.00%"
     *   },
     *   "message": "success"
     * }
     */
    @GetMapping("/progress")
    public JsonResult<ImportProgress> getImportProgress(@RequestParam("taskId") String taskId) {
        try {
            ImportProgress progress = importService.getImportProgress(taskId);
            return JsonResult.ok(progress);
        } catch (Exception e) {
            log.error("查询导入进度失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, "查询导入进度失败"));
        }
    }
}