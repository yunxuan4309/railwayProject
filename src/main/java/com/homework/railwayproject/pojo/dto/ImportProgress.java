package com.homework.railwayproject.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 导入进度跟踪DTO类
 * 
 * 用于跟踪文件导入任务的进度状态，支持异步导入和进度查询功能
 * 
 * 字段说明：
 * - taskId: 任务唯一标识符
 * - status: 任务状态（PENDING-等待中，RUNNING-运行中，COMPLETED-已完成，FAILED-失败）
 * - totalRecords: 总记录数
 * - processedRecords: 已处理记录数
 * - successCount: 成功处理记录数
 * - failedCount: 失败记录数
 * - message: 任务执行过程中的消息
 */
@Data
public class ImportProgress implements Serializable {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_RUNNING = "RUNNING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";
    
    private String taskId;
    private String status;
    private int totalRecords;
    private int processedRecords;
    private int successCount;
    private int failedCount;
    private String message;
    
    public ImportProgress() {
        this.status = STATUS_PENDING;
        this.totalRecords = 0;
        this.processedRecords = 0;
        this.successCount = 0;
        this.failedCount = 0;
    }
    
    public ImportProgress(String taskId) {
        this.taskId = taskId;
        this.status = STATUS_PENDING;
        this.totalRecords = 0;
        this.processedRecords = 0;
        this.successCount = 0;
        this.failedCount = 0;
    }
    
    public double getProgressPercentage() {
        if (totalRecords == 0) {
            return status.equals(STATUS_COMPLETED) ? 100.0 : 0.0;
        }
        return Math.min(100.0, (double) processedRecords / totalRecords * 100.0);
    }
}