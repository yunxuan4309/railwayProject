package com.homework.railwayproject.pojo.entity;

/**
 * 站点角色枚举
 */
public enum StationRole {
    /**
     * 始发站 - 主要用于发送旅客的站点
     */
    DEPARTURE_STATION,
    
    /**
     * 终到站 - 主要用于到达旅客的站点
     */
    ARRIVAL_STATION,
    
    /**
     * 中转站 - 主要用于旅客换乘的站点
     */
    TRANSFER_STATION,
    
    /**
     * 通过站 - 既不是始发站也不是终到站或主要中转站的普通站点
     */
    PASS_THROUGH_STATION
}