package com.homework.railwayproject.pojo.entity;

/**
 * 容量匹配度状态枚举
 */
public enum CapacityMatchingStatus {
    /**
     * 绿灯 - 容量充足 (匹配度低于70%)
     */
    GREEN("GREEN", "绿灯 - 容量充足"),
    
    /**
     * 黄灯 - 容量紧张 (匹配度70%-90%)
     */
    YELLOW("YELLOW", "黄灯 - 容量紧张"),
    
    /**
     * 红灯 - 容量超载 (匹配度超过90%)
     */
    RED("RED", "红灯 - 容量超载");
    
    private final String code;
    private final String description;
    
    CapacityMatchingStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据匹配度百分比获取状态
     * 
     * @param matchingPercentage 匹配度百分比
     * @return 容量匹配度状态
     */
    public static CapacityMatchingStatus getStatusByPercentage(double matchingPercentage) {
        if (matchingPercentage < 70.0) {
            return GREEN;
        } else if (matchingPercentage <= 90.0) {
            return YELLOW;
        } else {
            return RED;
        }
    }
}