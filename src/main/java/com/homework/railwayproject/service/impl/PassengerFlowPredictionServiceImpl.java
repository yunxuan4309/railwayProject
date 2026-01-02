package com.homework.railwayproject.service.impl;

import com.homework.railwayproject.mapper.PassengerFlowPredictionMapper;
import com.homework.railwayproject.mapper.StationPassengerFlowStatMapper;

import com.homework.railwayproject.pojo.dto.HistoricalPassengerFlowData;
import com.homework.railwayproject.pojo.dto.PassengerFlowPredictionDTO;
import com.homework.railwayproject.pojo.entity.PassengerFlowPrediction;
import com.homework.railwayproject.service.PassengerFlowPredictionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PassengerFlowPredictionServiceImpl implements PassengerFlowPredictionService {

    @Autowired
    private StationPassengerFlowStatMapper stationPassengerFlowStatMapper;

    @Autowired
    private PassengerFlowPredictionMapper passengerFlowPredictionMapper;

    @Override
    public List<PassengerFlowPredictionDTO> predictPassengerFlowByMovingAverage(
            Integer siteId, 
            LocalDate predictionDate, 
            int daysForPrediction) {
        
        log.info("开始使用移动平均算法预测站点{}在{}的客流量，使用前{}天数据", siteId, predictionDate, daysForPrediction);
        
        // 获取预测日期前N天的历史数据
        LocalDate endDate = predictionDate.minusDays(1);
        LocalDate startDate = endDate.minusDays(daysForPrediction - 1);
        
        List<Object[]> historicalData = getHistoricalPassengerFlowData(siteId, startDate, endDate);
        
        if (historicalData.isEmpty()) {
            log.warn("没有找到站点{}在{}到{}之间的历史数据", siteId, startDate, endDate);
            return new ArrayList<>();
        }
        
        // 按小时聚合历史数据
        Map<LocalTime, List<Integer>> hourlyBoardingData = new HashMap<>();
        Map<LocalTime, List<Integer>> hourlyAlightingData = new HashMap<>();
        
        for (Object[] row : historicalData) {
            LocalTime time = convertToLocalTime(row[0]);
            Integer boardingCount = convertToInteger(row[1]);
            Integer alightingCount = convertToInteger(row[2]);
            
            if (time != null && boardingCount != null && alightingCount != null) {
                hourlyBoardingData.computeIfAbsent(time, k -> new ArrayList<>()).add(boardingCount);
                hourlyAlightingData.computeIfAbsent(time, k -> new ArrayList<>()).add(alightingCount);
            }
        }
        
        // 计算每个小时的平均值
        List<PassengerFlowPredictionDTO> predictions = new ArrayList<>();
        
        for (Map.Entry<LocalTime, List<Integer>> entry : hourlyBoardingData.entrySet()) {
            LocalTime time = entry.getKey();
            List<Integer> boardingList = entry.getValue();
            List<Integer> alightingList = hourlyAlightingData.get(time);
            
            // 计算平均值
            double avgBoarding = boardingList.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            double avgAlighting = alightingList.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            
            // 计算准确率（基于数据点的数量，数据点越多准确率越高）
            int dataPoints = Math.min(boardingList.size(), alightingList.size());
            double accuracy = calculateAccuracy(dataPoints, "移动平均");
            
            PassengerFlowPredictionDTO prediction = new PassengerFlowPredictionDTO();
            prediction.setSiteId(siteId);
            prediction.setPredictionDate(predictionDate);
            prediction.setPredictionTime(time);
            prediction.setPredictedBoardingCount((int) Math.round(avgBoarding));
            prediction.setPredictedAlightingCount((int) Math.round(avgAlighting));
            prediction.setPredictedTotalFlow((int) Math.round(avgBoarding + avgAlighting));
            prediction.setPredictionMethod("移动平均算法");
            prediction.setAccuracy(accuracy);
            
            predictions.add(prediction);
        }
        
        log.info("移动平均算法预测完成，共生成{}条预测数据", predictions.size());
        return predictions;
    }

    @Override
    public List<PassengerFlowPredictionDTO> predictPassengerFlowByPeriodicity(
            Integer siteId, 
            LocalDate predictionDate) {
        
        log.info("开始使用周期性分析算法预测站点{}在{}的客流量", siteId, predictionDate);
        
        // 获取前几周的数据，扩大查询范围以确保有足够的历史数据
        LocalDate endDate = predictionDate.minusDays(7);  // 从预测日期往前推7天
        LocalDate startDate = endDate.minusDays(56); // 查询过去8周的数据
        
        log.info("查询历史数据时间范围：{} 到 {}", startDate, endDate);
        
        // 获取历史同期数据
        List<Object[]> historicalData = getHistoricalPassengerFlowData(siteId, startDate, endDate);
        
        log.info("查询到{}条历史数据记录", historicalData.size());
        
        if (historicalData.isEmpty()) {
            log.warn("没有找到站点{}在{}到{}之间的历史数据，客流信息不足，无法预测", siteId, startDate, endDate);
            // 抛出异常或返回空列表，这里我们返回空列表，让控制器处理
            return new ArrayList<>();
        }
        
        // 按小时聚合历史同期数据
        Map<LocalTime, List<Integer>> hourlyBoardingData = new HashMap<>();
        Map<LocalTime, List<Integer>> hourlyAlightingData = new HashMap<>();
        
        for (Object[] row : historicalData) {
            LocalTime time = convertToLocalTime(row[0]);
            Integer boardingCount = convertToInteger(row[1]);
            Integer alightingCount = convertToInteger(row[2]);
            LocalDate travelDate = convertToLocalDate(row[3]);
            
            if (time != null && boardingCount != null && alightingCount != null && travelDate != null) {
                // 检查是否是相同的星期几
                if (isSameDayOfWeek(predictionDate, travelDate)) {
                    log.debug("找到相同星期几的数据: {} ({}), 时间: {}, 上客量: {}, 下客量: {}", 
                            travelDate, travelDate.getDayOfWeek(), time, boardingCount, alightingCount);
                    hourlyBoardingData.computeIfAbsent(time, k -> new ArrayList<>()).add(boardingCount);
                    hourlyAlightingData.computeIfAbsent(time, k -> new ArrayList<>()).add(alightingCount);
                }
            } else {
                log.debug("跳过无效数据行: time={}, boarding={}, alighting={}, date={}", 
                        row[0], row[1], row[2], row[3]);
            }
        }
        
        // 检查是否找到匹配的历史数据
        if (hourlyBoardingData.isEmpty()) {
            log.warn("没有找到与预测日期{}相同星期几的历史数据，客流信息不足，无法预测", predictionDate);
            // 没有找到匹配的数据，返回空列表
            return new ArrayList<>();
        }
        
        // 计算每个小时的平均值
        List<PassengerFlowPredictionDTO> predictions = new ArrayList<>();
        
        for (Map.Entry<LocalTime, List<Integer>> entry : hourlyBoardingData.entrySet()) {
            LocalTime time = entry.getKey();
            List<Integer> boardingList = entry.getValue();
            List<Integer> alightingList = hourlyAlightingData.get(time);
            
            if (!boardingList.isEmpty() && !alightingList.isEmpty()) {
                // 计算平均值
                double avgBoarding = boardingList.stream().mapToInt(Integer::intValue).average().orElse(0.0);
                double avgAlighting = alightingList.stream().mapToInt(Integer::intValue).average().orElse(0.0);
                
                // 计算准确率（基于数据点的数量，数据点越多准确率越高）
                int dataPoints = Math.min(boardingList.size(), alightingList.size());
                double accuracy = calculateAccuracy(dataPoints, "周期性分析");
                
                log.debug("时段{}的平均上客量: {}, 平均下客量: {}", time, avgBoarding, avgAlighting);
                
                PassengerFlowPredictionDTO prediction = new PassengerFlowPredictionDTO();
                prediction.setSiteId(siteId);
                prediction.setPredictionDate(predictionDate);
                prediction.setPredictionTime(time);
                prediction.setPredictedBoardingCount((int) Math.round(avgBoarding));
                prediction.setPredictedAlightingCount((int) Math.round(avgAlighting));
                prediction.setPredictedTotalFlow((int) Math.round(avgBoarding + avgAlighting));
                prediction.setPredictionMethod("周期性分析算法");
                prediction.setAccuracy(accuracy);
                
                predictions.add(prediction);
            }
        }
        
        log.info("周期性分析算法预测完成，共生成{}条预测数据", predictions.size());
        return predictions;
    }

    @Override
    public void savePredictions(List<PassengerFlowPrediction> predictions) {
        log.info("开始保存{}条客流预测数据", predictions.size());
        
        for (PassengerFlowPrediction prediction : predictions) {
            passengerFlowPredictionMapper.insertPrediction(prediction);
        }
        
        log.info("客流预测数据保存完成");
    }

    @Override
    public void savePredictionDTOs(List<PassengerFlowPredictionDTO> predictionDtos, String predictionMethod) {
        log.info("开始保存{}条客流预测DTO数据，预测方法：{}", predictionDtos.size(), predictionMethod);
        
        for (PassengerFlowPredictionDTO dto : predictionDtos) {
            PassengerFlowPrediction prediction = new PassengerFlowPrediction();
            prediction.setSiteId(dto.getSiteId());
            prediction.setSiteName(dto.getSiteName());
            prediction.setPredictionDate(dto.getPredictionDate());
            prediction.setPredictionTime(dto.getPredictionTime());
            prediction.setPredictedBoardingCount(dto.getPredictedBoardingCount());
            prediction.setPredictedAlightingCount(dto.getPredictedAlightingCount());
            prediction.setPredictedTotalFlow(dto.getPredictedTotalFlow());
            prediction.setPredictionMethod(dto.getPredictionMethod());
            prediction.setAccuracy(dto.getAccuracy());
            
            passengerFlowPredictionMapper.insertPrediction(prediction);
        }
        
        log.info("客流预测DTO数据保存完成");
    }

    @Override
    public List<Object[]> getHistoricalPassengerFlowData(Integer siteId, LocalDate startDate, LocalDate endDate) {
        log.debug("获取站点{}在{}到{}之间的历史客流数据", siteId, startDate, endDate);
        
        // 使用现有的mapper方法获取历史数据用于预测
        List<HistoricalPassengerFlowData> dataList = stationPassengerFlowStatMapper.getHistoricalDataForPrediction(siteId, startDate, endDate);
        
        // 将HistoricalPassengerFlowData列表转换为Object[]列表
        List<Object[]> result = new ArrayList<>();
        for (HistoricalPassengerFlowData data : dataList) {
            Object[] row = new Object[4];
            row[0] = data.getDepartTime();
            row[1] = data.getBoardingCount();
            row[2] = data.getAlightingCount();
            row[3] = data.getTravelDate();
            result.add(row);
        }
        
        return result;
    }

    @Override
    public List<PassengerFlowPrediction> getPredictionsBySiteAndDateRange(
            Integer siteId, 
            LocalDate startDate, 
            LocalDate endDate) {
        return passengerFlowPredictionMapper.selectPredictionsBySiteAndDateRange(siteId, startDate, endDate);
    }
    
    /**
     * 检查两个日期是否是相同的星期几
     * 
     * @param date1 日期1
     * @param date2 日期2
     * @return 如果是相同的星期几则返回true，否则返回false
     */
    private boolean isSameDayOfWeek(LocalDate date1, LocalDate date2) {
        return date1.getDayOfWeek() == date2.getDayOfWeek();
    }
    
    /**
     * 将对象转换为LocalTime
     * 
     * @param obj 要转换的对象
     * @return LocalTime对象，如果转换失败则返回null
     */
    private LocalTime convertToLocalTime(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof LocalTime) {
            return (LocalTime) obj;
        } else if (obj instanceof java.sql.Time) {
            return ((java.sql.Time) obj).toLocalTime();
        } else if (obj instanceof String) {
            try {
                return LocalTime.parse((String) obj);
            } catch (Exception e) {
                log.warn("无法将字符串转换为LocalTime: " + obj);
                return null;
            }
        } else {
            log.warn("无法将对象转换为LocalTime: " + obj);
            return null;
        }
    }
    
    /**
     * 将对象转换为LocalDate
     * 
     * @param obj 要转换的对象
     * @return LocalDate对象，如果转换失败则返回null
     */
    private LocalDate convertToLocalDate(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof LocalDate) {
            return (LocalDate) obj;
        } else if (obj instanceof java.sql.Date) {
            return ((java.sql.Date) obj).toLocalDate();
        } else if (obj instanceof String) {
            try {
                return LocalDate.parse((String) obj);
            } catch (Exception e) {
                log.warn("无法将字符串转换为LocalDate: " + obj);
                return null;
            }
        } else {
            log.warn("无法将对象转换为LocalDate: " + obj);
            return null;
        }
    }
    
    /**
     * 将对象转换为Integer
     * 
     * @param obj 要转换的对象
     * @return Integer对象，如果转换失败则返回null
     */
    private Integer convertToInteger(Object obj) {
        if (obj == null) {
            return 0;
        } else if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Number) {
            return ((Number) obj).intValue();
        } else if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                log.warn("无法将字符串转换为Integer: " + obj);
                return 0;
            }
        } else {
            log.warn("无法将对象转换为Integer: " + obj);
            return 0;
        }
    }
    
    /**
     * 根据数据点数量计算预测准确率
     * 
     * @param dataPoints 数据点数量
     * @param algorithmType 算法类型
     * @return 准确率值 (0.0-1.0)
     */
    private double calculateAccuracy(int dataPoints, String algorithmType) {
        // 基础准确率
        double baseAccuracy = "移动平均".equals(algorithmType) ? 0.65 : 0.70;
        
        // 根据数据点数量调整准确率，数据点越多准确率越高
        // 最多增加0.2的准确率提升
        double dataQualityBoost = Math.min(0.2, (dataPoints - 1) * 0.05);
        
        // 计算最终准确率，但不超过0.95
        double finalAccuracy = Math.min(0.95, baseAccuracy + dataQualityBoost);
        
        return finalAccuracy;
    }
}