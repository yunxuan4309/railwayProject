package com.homework.railwayproject.controller;

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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OD图表控制器
 * 提供OD（起始点-目的地）客流图表数据接口，专为前端可视化设计
 * 
 * 数据说明：
 * - origins/destinations: 站点名称列表，用于热力图表格的行列标签
 * - matrix: 二维数组，表示从起始站点到目标站点的客流量矩阵
 * - nodes: 图表节点，包含站点信息
 * - links/edges: 图表连接，表示站点间的客流关系
 * - value: 客流量值，用于视觉编码（颜色深浅、线宽等）
 * 
 * 应用场景：
 * 1. 矩阵热力图：展示站点间客流矩阵，颜色深浅表示客流量大小
 * 2. 桑基图：展示客流流向，线宽表示客流量
 * 3. 关系图：展示站点网络，连线粗细表示客流强度
 */
@Tag(name = "OD图表", description = "提供OD（起始点-目的地）客流图表数据接口")
@RestController
@RequestMapping("/api/od-chart")
public class ODChartController {

    @Autowired
    private ODHeatMapService odHeatMapService;

    /**
     * 获取OD矩阵数据（用于热力图表格）
     * 
     * 返回数据格式：
     * {
     *   "origins": ["北京南站", "上海虹桥", "广州南站"],  // 起始站点列表，用于表格行标签
     *   "destinations": ["上海虹桥", "广州南站", "深圳北站"], // 目标站点列表，用于表格列标签
     *   "matrix": [                                    // 客流量矩阵，用于热力图颜色映射
     *     [0, 1500, 800],                             // 北京南站到各站的客流量
     *     [1500, 0, 1200],                            // 上海虹桥到各站的客流量
     *     [800, 1200, 0]                              // 广州南站到各站的客流量
     *   ],
     *   "date": "2024-01-01"                          // 查询日期，用于前端显示
     * }
     * 
     * 前端作用：
     * - 用于ECharts热力图的xAxis和yAxis数据
     * - matrix数据用于热力图的series.data
     * - 颜色深浅表示客流量大小，便于识别主要客流通道
     * 
     * @param date 指定日期
     * @return OD矩阵数据
     */
    @Operation(summary = "获取OD矩阵数据（用于热力图表格）", description = "返回适合前端热力图表格展示的OD矩阵数据")
    @GetMapping("/matrix")
    public JsonResult<Object> getODMatrix(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<ODHeatMap> odHeatMapList = odHeatMapService.getODHeatMapByDate(date);
        
        // 构建OD矩阵数据
        Map<String, Object> matrixData = buildODMatrix(odHeatMapList);
        
        return JsonResult.ok(matrixData);
    }

    /**
     * 获取OD桑基图数据
     * 
     * 返回数据格式：
     * {
     *   "nodes": [                                    // 桑基图节点数据
     *     {"name": "北京南站"},                       // 站点名称，用于节点显示
     *     {"name": "上海虹桥"},                       // 站点名称，用于节点显示
     *     {"name": "广州南站"}                        // 站点名称，用于节点显示
     *   ],
     *   "links": [                                    // 桑基图连接数据
     *     {                                           // 单个连接数据
     *       "source": "北京南站",                     // 起始站点，用于连接起点
     *       "target": "上海虹桥",                     // 目标站点，用于连接终点
     *       "value": 1500                            // 客流量，用于线宽和颜色映射
     *     },
     *     {
     *       "source": "上海虹桥",
     *       "target": "广州南站", 
     *       "value": 1200
     *     }
     *   ],
     *   "date": "2024-01-01"                         // 查询日期，用于前端显示
     * }
     * 
     * 前端作用：
     * - nodes数据用于ECharts桑基图的节点配置
     * - links数据用于ECharts桑基图的连接配置
     * - value值决定连接线的宽度，直观展示客流强度
     * 
     * @param date 指定日期
     * @return 桑基图数据
     */
    @Operation(summary = "获取OD桑基图数据", description = "返回适合前端桑基图展示的OD流向数据")
    @GetMapping("/sankey")
    public JsonResult<Object> getODSankeyData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<ODHeatMap> odHeatMapList = odHeatMapService.getODHeatMapByDate(date);
        
        // 构建桑基图数据
        Map<String, Object> sankeyData = buildSankeyData(odHeatMapList);
        
        return JsonResult.ok(sankeyData);
    }

    /**
     * 获取OD关系图数据
     * 
     * 返回数据格式：
     * {
     *   "nodes": [                                    // 关系图节点数据
     *     {                                           // 单个节点数据
     *       "id": "北京南站",                         // 节点唯一标识
     *       "name": "北京南站",                       // 节点名称，用于显示
     *       "symbolSize": 10                         // 节点大小，可基于重要性调整
     *     },
     *     {
     *       "id": "上海虹桥",
     *       "name": "上海虹桥", 
     *       "symbolSize": 10
     *     }
     *   ],
     *   "edges": [                                    // 关系图边数据
     *     {                                           // 单个边数据
     *       "source": "北京南站",                     // 起始节点，用于边的起点
     *       "target": "上海虹桥",                     // 目标节点，用于边的终点
     *       "value": 1500,                           // 客流量，用于线宽映射
     *       "lineStyle": {                           // 线条样式
     *         "width": 15                            // 线宽，基于客流量计算
     *       }
     *     }
     *   ],
     *   "date": "2024-01-01"                         // 查询日期，用于前端显示
     * }
     * 
     * 前端作用：
     * - nodes数据用于ECharts关系图的节点配置
     * - edges数据用于ECharts关系图的边配置
     * - value和lineStyle.width用于视觉编码，展示连接强度
     * 
     * @param date 指定日期
     * @return 关系图数据
     */
    @Operation(summary = "获取OD关系图数据", description = "返回适合前端关系图展示的节点和连线数据")
    @GetMapping("/relation")
    public JsonResult<Object> getODRelationData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<ODHeatMap> odHeatMapList = odHeatMapService.getODHeatMapByDate(date);
        
        // 构建关系图数据
        Map<String, Object> relationData = buildRelationData(odHeatMapList);
        
        return JsonResult.ok(relationData);
    }

    /**
     * 构建OD矩阵数据（适合热力图表格）
     * 
     * 用途：为前端热力图提供行列标签和矩阵数据
     * 
     * 修复说明：现在矩阵使用与关系图和桑基图相同的站点集合，确保数据一致性
     * 
     * @param odHeatMapList OD热力图数据列表
     * @return 包含origins、destinations、matrix和date的Map
     */
    private Map<String, Object> buildODMatrix(List<ODHeatMap> odHeatMapList) {
        // 获取所有唯一的站点（合并起始站点和目标站点），与关系图和桑基图保持一致
        // 同时去除站点名称的前后空格，确保索引匹配
        List<String> allStations = odHeatMapList.stream()
                .flatMap(od -> java.util.stream.Stream.of(
                    od.getOriginStationName().trim(), 
                    od.getDestStationName().trim()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        // 矩阵的行和列都使用相同的站点列表，确保一致性
        List<String> origins = allStations;
        List<String> destinations = allStations;
        
        // 创建矩阵数据
        int[][] matrix = new int[origins.size()][destinations.size()];
        
        // 初始化矩阵为0
        for (int i = 0; i < origins.size(); i++) {
            for (int j = 0; j < destinations.size(); j++) {
                matrix[i][j] = 0;
            }
        }
        
        // 填充矩阵 - 填充实际存在的OD对
        for (ODHeatMap od : odHeatMapList) {
            String originName = od.getOriginStationName().trim();
            String destName = od.getDestStationName().trim();
            
            int originIndex = allStations.indexOf(originName);
            int destIndex = allStations.indexOf(destName);
            
            if (originIndex >= 0 && destIndex >= 0) {
                matrix[originIndex][destIndex] = od.getPassengerFlow();
            }
        }
        
        return Map.of(
            "origins", origins,
            "destinations", destinations,
            "matrix", matrix,
            "date", odHeatMapList.isEmpty() ? "" : odHeatMapList.get(0).getTravelDate()
        );
    }

    /**
     * 构建桑基图数据
     * 
     * 用途：为前端桑基图提供节点和连接数据
     * 
     * @param odHeatMapList OD热力图数据列表
     * @return 包含nodes、links和date的Map
     */
    private Map<String, Object> buildSankeyData(List<ODHeatMap> odHeatMapList) {
        // 提取所有唯一的站点作为节点，去除站点名称的前后空格
        List<String> allStations = odHeatMapList.stream()
                .flatMap(od -> java.util.stream.Stream.of(
                    od.getOriginStationName().trim(), 
                    od.getDestStationName().trim()))
                .distinct()
                .collect(Collectors.toList());
        
        // 创建节点列表
        List<Map<String, Object>> nodes = allStations.stream()
                .<Map<String, Object>>map(station -> Map.of("name", station))
                .collect(Collectors.toList());
        
        // 创建链接列表，同样去除站点名称的前后空格
        List<Map<String, Object>> links = odHeatMapList.stream()
                .<Map<String, Object>>map(od -> Map.of(
                    "source", od.getOriginStationName().trim(),
                    "target", od.getDestStationName().trim(),
                    "value", od.getPassengerFlow()
                ))
                .collect(Collectors.toList());
        
        return Map.of(
            "nodes", nodes,
            "links", links,
            "date", odHeatMapList.isEmpty() ? "" : odHeatMapList.get(0).getTravelDate()
        );
    }

    /**
     * 构建关系图数据
     * 
     * 用途：为前端关系图提供节点和边数据
     * 
     * @param odHeatMapList OD热力图数据列表
     * @return 包含nodes、edges和date的Map
     */
    private Map<String, Object> buildRelationData(List<ODHeatMap> odHeatMapList) {
        // 提取所有唯一的站点作为节点，去除站点名称的前后空格
        List<String> allStations = odHeatMapList.stream()
                .flatMap(od -> java.util.stream.Stream.of(
                    od.getOriginStationName().trim(), 
                    od.getDestStationName().trim()))
                .distinct()
                .collect(Collectors.toList());
        
        // 创建节点列表
        List<Map<String, Object>> nodes = allStations.stream()
                .<Map<String, Object>>map(station -> Map.of(
                    "id", station,
                    "name", station,
                    "symbolSize", 10 // 可以根据站点重要性调整大小
                ))
                .collect(Collectors.toList());
        
        // 创建边列表，同样去除站点名称的前后空格
        List<Map<String, Object>> edges = odHeatMapList.stream()
                .<Map<String, Object>>map(od -> Map.of(
                    "source", od.getOriginStationName().trim(),
                    "target", od.getDestStationName().trim(),
                    "value", od.getPassengerFlow(),
                    "lineStyle", Map.of("width", Math.max(1, od.getPassengerFlow() / 100.0)) // 根据客流量调整线宽
                ))
                .collect(Collectors.toList());
        
        return Map.of(
            "nodes", nodes,
            "edges", edges,
            "date", odHeatMapList.isEmpty() ? "" : odHeatMapList.get(0).getTravelDate()
        );
    }
}