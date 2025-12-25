package com.homework.railwayproject.task;

import com.homework.railwayproject.service.BusyIndexStatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 站点繁忙指数统计定时任务
 * 每10分钟执行一次
 */
@Slf4j
@Component
public class BusyIndexStatTask {

    @Autowired
    private BusyIndexStatService busyIndexStatService;

    /**
     * 每10分钟执行一次站点繁忙指数统计任务
     * cron表达式: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void calculateAndCacheTop20BusyIndex() {
        try {
            busyIndexStatService.calculateAndCacheTop20BusyIndexStations();
        } catch (Exception e) {
            log.error("定时统计站点繁忙指数前20名失败", e);
        }
    }
}