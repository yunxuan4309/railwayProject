package com.homework.railwayproject.task;

import com.homework.railwayproject.service.LineOptimizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class SectionStatisticsTask {

    @Autowired
    private LineOptimizationService lineOptimizationService;

    /**
     * 每天凌晨2点统计前一天的区间客流数据
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void calculateDailyStatistics() {
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            lineOptimizationService.calculateAndSaveSectionStatistics(yesterday);
            log.info("完成{}的区间客流统计", yesterday);
        } catch (Exception e) {
            log.error("区间客流统计任务失败", e);
        }
    }

    /**
     * 每天凌晨3点检测连续过载区间
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void detectOverloadSections() {
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            lineOptimizationService.detectContinuousOverloadSections(yesterday);
            log.info("完成过载区间检测");
        } catch (Exception e) {
            log.error("过载区间检测任务失败", e);
        }
    }
}