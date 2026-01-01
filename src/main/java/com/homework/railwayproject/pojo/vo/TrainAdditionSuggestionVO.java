package com.homework.railwayproject.pojo.vo;

import lombok.Data;

import java.time.LocalTime;

@Data
public class TrainAdditionSuggestionVO {
    private String lineCode;               // 线路编码
    private String section;                // 需要加车的区间
    private String suggestedTrainNumber;   // 建议车次号
    private LocalTime departureTime;       // 建议发车时间
    private LocalTime arrivalTime;         // 建议到达时间
    private Integer carriageCount;         // 建议编组
    private String trainType;              // 建议车型
    private String reason;                 // 建议原因
    private Double expectedLoadRate;       // 预计满载率
}