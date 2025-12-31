package com.homework.railwayproject.pojo.dto;

import lombok.Data;

import java.io.Serializable;

//Author:[谢云轩]
//QQ:[1721476339]
//ID:[632307060623]
//Date:2025/11/26
//Time:8:50
@Data
public class ImportResult implements Serializable {
    private int successCount;
    private int failedCount;

    public ImportResult(int successCount, int failedCount) {
        this.successCount = successCount;
        this.failedCount = failedCount;
    }

    // 添加默认构造函数
    public ImportResult() {
        this(0, 0);
    }
}