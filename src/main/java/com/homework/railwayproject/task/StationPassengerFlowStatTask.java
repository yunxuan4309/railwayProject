package com.homework.railwayproject.task;

import com.homework.railwayproject.service.StationPassengerFlowStatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 站点客流统计定时任务
 */
@Slf4j
@Component
public class StationPassengerFlowStatTask {
    
    @Autowired
    private StationPassengerFlowStatService stationPassengerFlowStatService;
    
    /**
     * 每天06:30执行一次站点客流统计任务
     * cron表达式: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 30 6 * * ?")
    public void calculateAndCacheTop20Stations() {
        try {
            stationPassengerFlowStatService.calculateAndCacheTop20Stations();
        } catch (Exception e) {
            log.error("定时统计站点客流前20名失败", e);
        }
    }
}