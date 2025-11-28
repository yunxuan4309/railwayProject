package com.homework.railwayproject.controller;


import com.homework.railwayproject.exception.ServiceException;
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
    @PostMapping("/high-speed-passenger/clean")
    public JsonResult<ImportResult> cleanHighSpeedPassenger(@RequestParam("filename") String filename) {
        try {
            ImportResult result = importService.cleanData(filename);
            return JsonResult.ok(result);
        } catch (Exception e) {
            log.error("清洗失败", e);
            return JsonResult.fail(new ServiceException(ServiceCode.ERROR_UNKNOWN, "清洗失败"));
        }
    }
}