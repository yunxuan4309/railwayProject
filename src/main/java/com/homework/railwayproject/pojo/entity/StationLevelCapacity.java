package com.homework.railwayproject.pojo.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 站点等级容量配置实体类
 * 用于定义不同等级站点的容客量标准
 */
@Data
public class StationLevelCapacity implements Serializable {
    
    /**
     * 站点等级
     */
    private String stationLevel;
    
    /**
     * 站点等级名称
     */
    private String levelName;
    
    /**
     * 基础容客量（每小时最大容纳客流量）
     */
    private Integer baseCapacity;
    
    /**
     * 每个站台的额外容客量
     */
    private Integer platformCapacity;
    
    /**
     * 每个检票口的额外容客量
     */
    private Integer gateCapacity;
    
    /**
     * 计算实际容客量
     * 实际容客量 = 基础容客量 + 站台数量 * 每个站台容客量 + 检票口数量 * 每个检票口容客量
     * 
     * @param platformCount 站台数量
     * @param gateCount 检票口数量
     * @return 实际容客量
     */
    public Integer calculateActualCapacity(Integer platformCount, Integer gateCount) {
        if (platformCount == null) platformCount = 0;
        if (gateCount == null) gateCount = 0;
        
        Integer platformAdditionalCapacity = platformCount * this.platformCapacity;
        Integer gateAdditionalCapacity = gateCount * this.gateCapacity;
        
        return this.baseCapacity + platformAdditionalCapacity + gateAdditionalCapacity;
    }
    
    /**
     * 构造函数
     */
    public StationLevelCapacity() {}
    
    public StationLevelCapacity(String stationLevel, String levelName, Integer baseCapacity, 
                               Integer platformCapacity, Integer gateCapacity) {
        this.stationLevel = stationLevel;
        this.levelName = levelName;
        this.baseCapacity = baseCapacity;
        this.platformCapacity = platformCapacity;
        this.gateCapacity = gateCapacity;
    }
    
    /**
     * 获取默认的站点等级容量配置
     * 
     * @return 默认配置列表
     */
    public static List<StationLevelCapacity> getDefaultConfigurations() {
        List<StationLevelCapacity> configurations = new ArrayList<>();
        
        // 特等站：国家级大型枢纽站，如北京站、上海站等
        configurations.add(new StationLevelCapacity("特等", "特等站", 15000, 800, 500));
        
        // 一等站：大型区域枢纽站
        configurations.add(new StationLevelCapacity("一等", "一等站", 10000, 600, 400));
        
        // 二等站：中型枢纽站或重要地级市站
        configurations.add(new StationLevelCapacity("二等", "二等站", 6000, 400, 300));
        
        // 三等站：一般地级市站或重要县级市站
        configurations.add(new StationLevelCapacity("三等", "三等站", 3000, 250, 200));
        
        // 四等站：县级市站或重要乡镇站
        configurations.add(new StationLevelCapacity("四等", "四等站", 1500, 150, 120));
        
        // 五等站：一般乡镇站或乘降所
        configurations.add(new StationLevelCapacity("五等", "五等站", 500, 80, 60));
        
        return configurations;
    }
    
    /**
     * 根据站点等级获取容量配置
     * 
     * @param stationLevel 站点等级
     * @return 容量配置
     */
    public static StationLevelCapacity getByStationLevel(String stationLevel) {
        for (StationLevelCapacity config : getDefaultConfigurations()) {
            if (config.getStationLevel().equals(stationLevel)) {
                return config;
            }
        }
        // 如果未找到对应等级，返回五等站配置作为默认值
        return new StationLevelCapacity("五等", "五等站", 500, 80, 60);
    }
}