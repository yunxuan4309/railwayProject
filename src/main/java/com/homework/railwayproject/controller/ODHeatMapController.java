package com.homework.railwayproject.controller;

import com.homework.railwayproject.pojo.dto.ODHeatMapDTO;
import com.homework.railwayproject.pojo.entity.ODHeatMap;
import com.homework.railwayproject.service.ODHeatMapService;
import com.homework.railwayproject.web.JsonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * OD热力图控制器
 * 提供OD（起始点-目的地）客流热力图数据接口
 * 
 * 数据说明：
 * - originStationId/destStationId: 站点唯一标识，用于数据关联和前端展示
 * - originStationName/destStationName: 站点名称，用于前端界面展示
 * - passengerFlow: 客流量（上客量+下客量），用于热力图颜色深浅、桑基图线宽等视觉编码
 * - heatValue: 热力值（该OD对客流量/总客流量），用于归一化显示不同日期间的相对热度
 * - rank: 排名（按客流量排序），用于前端数据排序和优先级展示
 * 
 * 应用场景：
 * 1. 热力图表格：展示站点间客流矩阵，颜色深浅表示客流量大小
 * 2. 桑基图：展示客流流向，线宽表示客流量
 * 3. 关系图：展示站点网络，连线粗细表示客流强度
 */
@Tag(name = "OD热力图", description = "提供OD（起始点-目的地）客流热力图数据接口")
@RestController
@RequestMapping("/api/od-heatmap")
public class ODHeatMapController {

    @Autowired
    private ODHeatMapService odHeatMapService;

    /**
     * 获取指定日期的OD热力图数据
     * 
     * 返回数据格式：
     * [
     *   {
     *     "originStationId": 1,           // 起始站点ID，用于数据关联
     *     "originStationName": "北京南站", // 起始站点名称，用于前端展示
     *     "destStationId": 2,             // 目标站点ID，用于数据关联
     *     "destStationName": "上海虹桥",   // 目标站点名称，用于前端展示
     *     "passengerFlow": 1500,          // 客流量，用于热力图颜色深浅、桑基图线宽等视觉编码
     *     "travelDate": "2024-01-01",     // 乘车日期，用于前端显示和数据分组
     *     "heatValue": 0.2500,            // 热力值（该OD对客流量/总客流量），用于归一化显示
     *     "rank": 1                       // 排名（按客流量排序），用于前端数据排序
     *   }
     * ]
     * 
     * 前端作用：
     * - 用于热力图表格的行/列标签
     * - 用于桑基图的节点和连线
     * - 用于关系图的节点和边
     * - 用于矩阵图的数据填充
     * 
     * @param date 指定日期
     * @return OD热力图数据列表
     */
    @Operation(summary = "获取指定日期的OD热力图数据", description = "返回指定日期的起始点-目的地客流数据")
    @GetMapping("/by-date")
    public JsonResult<List<ODHeatMap>> getODHeatMapByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ODHeatMap> odHeatMapList = odHeatMapService.getODHeatMapByDate(date);
        return JsonResult.ok(odHeatMapList);
    }

    /**
     * 获取日期范围的OD热力图数据
     * 
     * 返回数据格式：
     * [
     *   {
     *     "originStationId": 1,           // 起始站点ID，用于数据关联
     *     "originStationName": "北京南站", // 起始站点名称，用于前端展示
     *     "destStationId": 2,             // 目标站点ID，用于数据关联
     *     "destStationName": "上海虹桥",   // 目标站点名称，用于前端展示
     *     "passengerFlow": 10500,         // 日期范围内的总客流量，用于热力图颜色深浅
     *     "travelDate": "2024-01-01 to 2024-01-07", // 日期范围，用于前端显示
     *     "heatValue": 0.2500,            // 热力值，用于归一化显示
     *     "rank": 1                       // 排名，用于前端数据排序
     *   }
     * ]
     * 
     * 前端作用：
     * - 用于分析一段时间内的客流趋势
     * - 用于对比不同时间段的客流模式
     * - 用于长期客流预测和规划
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return OD热力图数据列表
     */
    @Operation(summary = "获取日期范围的OD热力图数据", description = "返回指定日期范围内的起始点-目的地客流数据")
    @GetMapping("/by-date-range")
    public JsonResult<List<ODHeatMap>> getODHeatMapByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ODHeatMap> odHeatMapList = odHeatMapService.getODHeatMapByDateRange(startDate, endDate);
        return JsonResult.ok(odHeatMapList);
    }

    /**
     * 获取指定出发站点的OD热力图数据
     * 
     * 返回数据格式：
     * [
     *   {
     *     "originStationId": 1,           // 固定为查询的出发站点ID
     *     "originStationName": "北京南站", // 固定为查询的出发站点名称
     *     "destStationId": 2,             // 到达站点ID，用于数据关联
     *     "destStationName": "上海虹桥",   // 到达站点名称，用于前端展示
     *     "passengerFlow": 1500,          // 从该站点出发的客流量
     *     "travelDate": "2024-01-01",     // 乘车日期，用于前端显示
     *     "heatValue": 0.5000,            // 相对于该站点总出发量的热力值
     *     "rank": 1                       // 排名，用于前端数据排序
     *   }
     * ]
     * 
     * 前端作用：
     * - 用于分析特定站点的辐射能力
     * - 用于展示站点的客流分布情况
     * - 用于优化站点服务资源配置
     * 
     * @param stationId 站点ID
     * @param date 指定日期
     * @return OD热力图数据列表
     */
    @Operation(summary = "获取指定出发站点的OD热力图数据", description = "返回从指定站点出发的客流热力图数据")
    @GetMapping("/by-departure-station")
    public JsonResult<List<ODHeatMap>> getODHeatMapByDepartureStation(
            @RequestParam Integer stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ODHeatMap> odHeatMapList = odHeatMapService.getODHeatMapByDepartureStation(stationId, date);
        return JsonResult.ok(odHeatMapList);
    }

    /**
     * 获取指定到达站点的OD热力图数据
     * 
     * 返回数据格式：
     * [
     *   {
     *     "originStationId": 1,           // 出发站点ID，用于数据关联
     *     "originStationName": "北京南站", // 出发站点名称，用于前端展示
     *     "destStationId": 2,             // 固定为查询的到达站点ID
     *     "destStationName": "上海虹桥",   // 固定为查询的到达站点名称
     *     "passengerFlow": 1500,          // 到达该站点的客流量
     *     "travelDate": "2024-01-01",     // 乘车日期，用于前端显示
     *     "heatValue": 0.6000,            // 相对于该站点总到达量的热力值
     *     "rank": 1                       // 排名，用于前端数据排序
     *   }
     * ]
     * 
     * 前端作用：
     * - 用于分析特定站点的吸引力
     * - 用于展示站点的客流来源分布
     * - 用于评估站点的重要性和影响力
     * 
     * @param stationId 站点ID
     * @param date 指定日期
     * @return OD热力图数据列表
     */
    @Operation(summary = "获取指定到达站点的OD热力图数据", description = "返回到达指定站点的客流热力图数据")
    @GetMapping("/by-arrival-station")
    public JsonResult<List<ODHeatMap>> getODHeatMapByArrivalStation(
            @RequestParam Integer stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ODHeatMap> odHeatMapList = odHeatMapService.getODHeatMapByArrivalStation(stationId, date);
        return JsonResult.ok(odHeatMapList);
    }

    /**
     * 获取指定日期的OD热力图数据（DTO格式）
     * 
     * 返回数据格式：
     * {
     *   "odData": [
     *     {
     *       "originStation": {
     *         "stationId": 1,             // 起始站点ID，用于数据关联
     *         "stationName": "北京南站"    // 起始站点名称，用于前端展示
     *       },
     *       "destStation": {
     *         "stationId": 2,             // 目标站点ID，用于数据关联
     *         "stationName": "上海虹桥"    // 目标站点名称，用于前端展示
     *       },
     *       "passengerFlow": 1500,        // 客流量，用于热力图颜色深浅
     *       "dateRange": "2024-01-01",    // 日期范围，用于前端显示
     *       "heatValue": 0.2500           // 热力值，用于归一化显示
     *     }
     *   ],
     *   "dateRange": "2024-01-01",        // 查询的日期范围
     *   "totalRecords": 1                 // 总记录数，用于前端分页或统计
     * }
     * 
     * 前端作用：
     * - 提供结构化的数据格式，便于前端处理
     * - 包含额外的元数据，用于界面展示
     * - 适合复杂的数据展示需求
     * 
     * @param date 指定日期
     * @return 格式化的OD热力图数据
     */
    @Operation(summary = "获取指定日期的OD热力图数据（DTO格式）", description = "返回适合前端展示的OD热力图数据")
    @GetMapping("/dto-by-date")
    public JsonResult<ODHeatMapDTO.ODHeatMapListDTO> getODHeatMapDTOByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ODHeatMap> odHeatMapList = odHeatMapService.getODHeatMapByDate(date);
        String dateRange = date.toString();
        ODHeatMapDTO.ODHeatMapListDTO dto = odHeatMapService.convertToDTO(odHeatMapList, dateRange);
        return JsonResult.ok(dto);
    }
}