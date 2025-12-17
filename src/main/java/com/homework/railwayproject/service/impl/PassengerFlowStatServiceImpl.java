package com.homework.railwayproject.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homework.railwayproject.mapper.PassengerFlowStatMapper;
import com.homework.railwayproject.pojo.entity.PassengerFlowStat;
import com.homework.railwayproject.service.PassengerFlowStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * 客流统计服务实现类
 */
@Service
public class PassengerFlowStatServiceImpl extends ServiceImpl<PassengerFlowStatMapper, PassengerFlowStat> implements PassengerFlowStatService {

    @Autowired
    private PassengerFlowStatMapper passengerFlowStatMapper;

    @Override
    public List<PassengerFlowStat> getDailyStat(LocalDate startDate, LocalDate endDate) {
        return passengerFlowStatMapper.selectDailyStat(startDate, endDate);
    }

    @Override
    public List<PassengerFlowStat> getWeeklyStat(LocalDate startDate, LocalDate endDate) {
        return passengerFlowStatMapper.selectWeeklyStat(startDate, endDate);
    }

    @Override
    public List<PassengerFlowStat> getMonthlyStat(LocalDate startDate, LocalDate endDate) {
        return passengerFlowStatMapper.selectMonthlyStat(startDate, endDate);
    }


    @Override
    public List<PassengerFlowStat> calculateRingGrowthRate(List<PassengerFlowStat> stats) {
        for (int i = 0; i < stats.size(); i++) {
            PassengerFlowStat current = stats.get(i);
            if (i > 0) {
                PassengerFlowStat previous = stats.get(i - 1);
                if (previous.getPassengerFlow() != null && previous.getPassengerFlow() != 0) {
                    // 环比增长率 = (本期数 - 上期数) / 上期数 * 100%
                    BigDecimal ringGrowth = new BigDecimal(current.getPassengerFlow() - previous.getPassengerFlow())
                            .divide(new BigDecimal(previous.getPassengerFlow()), 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal(100));
                    current.setComparisonRate(ringGrowth);
                }
            }
        }
        return stats;
    }

    @Override
    public List<PassengerFlowStat> calculateYearOnYearGrowthRate(List<PassengerFlowStat> stats) {
        // 在实际应用中，这里应该查询去年同期数据进行比较
        // 此处简化处理，暂不实现具体逻辑
        for (PassengerFlowStat stat : stats) {
            // 占位符，实际应根据业务逻辑计算
            stat.setYearOnYearRate(BigDecimal.ZERO);
        }
        return stats;
    }

    @Override
    public List<PassengerFlowStat> markHolidays(List<PassengerFlowStat> stats) {
        // 常见的中国法定节假日日期（不考虑调休）
        // 实际项目中可以从数据库或第三方API获取准确的节假日信息
        for (PassengerFlowStat stat : stats) {
            LocalDate date = stat.getBeginDate();
            if (isHoliday(date)) {
                stat.setIsHoliday(1);
            } else {
                stat.setIsHoliday(0);
            }
        }
        return stats;
    }
    /**
     * 判断指定日期是否为节假日（简化的实现）
     * @param date 日期
     * @return 是否为节假日
     */
    private boolean isHoliday(LocalDate date) {
        // 这里简单列举一些常见的固定日期节假日
        // 实际应用中应该有完整的节假日数据库或使用第三方API
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        // 元旦
        if (month == 1 && day == 1) return true;

        // 劳动节
        if (month == 5 && day == 1) return true;

        // 国庆节
        if (month == 10 && day >= 1 && day <= 3) return true;

        return false;
    }
}