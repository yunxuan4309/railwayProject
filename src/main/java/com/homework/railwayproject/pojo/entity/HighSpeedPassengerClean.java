package com.homework.railwayproject.pojo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 高铁客运清洗数据实体类
 * 对应Tickets_Clean表，每条记录代表一张有效车票（合并4条原始记录）
 *
 * @author 谢云轩
 * @date 2025/11/18
 */
@Data
public class HighSpeedPassengerClean extends BaseEntity{

    /**
     * 车票唯一ID（清洗后生成）
     * 原始字段：序号
     * 生成规则：列车编码_运行日期_归一化序列（原始序号/4取整）
     * 示例：148_20150101_382207
     * 用途：确保同一车票的4条原始记录合并为1条有效数据后的主键
     */
    private String ticketId;

    /**
     * 运营线路编码
     * 原始字段：运营线路编码
     * 清洗规则：直接保留，业务核心字段
     * 用途：线路负载分析、断面满载率计算
     */
    private String operationLineCode;

    /**
     * 列车编码
     * 原始字段：列车编码
     * 清洗规则：直接保留，业务核心字段
     * 用途：车次维度分析、列车运量关联
     */
    private Integer trainCode;

    /**
     * 原始站点ID（数据验证专用）
     * 原始字段：站点id
     * 清洗规则：保留原始值用于清洗过程验证与溯源
     * 用途：验证上车站点/下车站点推导正确性，支持版本回滚排查
     */
    private Integer originalSiteId;

    /**
     * 上车站点ID（清洗推导）
     * 原始字段：站点id + 上客量
     * 清洗规则：从上客量=1的记录中提取对应的站点id
     * 用途：OD流分析、站点繁忙指数计算
     */
    private Integer departStationId;

    /**
     * 下车站点ID（清洗推导）
     * 原始字段：站点id + 下客量
     * 清洗规则：从下客量=1的记录中提取对应的站点id
     * 用途：OD流分析、站点繁忙指数计算
     */
    private Integer arriveStationId;

    /**
     * 线路站点id
     * 原始字段：线路站点id
     * 清洗规则：待关联保留，当前为NULL但保留结构用于后续扩展
     * 用途：未来关联Line_Stations表进行站点层级关系分析
     */
    private Integer lineSiteId;

    /**
     * 上行线编码
     * 原始字段：上行线编码
     * 清洗规则：待关联保留，当前为NULL但保留结构用于后续扩展
     * 用途：列车运行方向判断、上下行客流区分
     */
    private String uplineCode;

    /**
     * 乘车日期
     * 原始字段：运行日期
     * 清洗规则：直接保留，业务核心字段
     * 用途：时间维度统计分析、日期聚合
     */
    private LocalDate travelDate;

    /**
     * 出发时间（格式转换后）
     * 原始字段：运行时间
     * 清洗规则：从"749"格式转换为"07:49:00"标准时间格式
     * 用途：高峰时段识别、时刻分析
     */
    private LocalTime departTime;

    /**
     * 与起点站距序
     * 原始字段：与起点站距序
     * 清洗规则：直接保留，待关联字段
     * 用途：计算运行里程、区间定位
     */
    private Integer distanceSeq;

    /**
     * 上客量
     * 原始字段：上客量
     * 清洗规则：直接保留，业务核心字段
     * 用途：站点发送量统计、繁忙指数计算（发送量×0.4）
     */
    private Integer boardingCount;

    /**
     * 下客量
     * 原始字段：下客量
     * 清洗规则：直接保留，业务核心字段
     * 用途：站点到达量统计、繁忙指数计算（到达量×0.6）
     */
    private Integer alightingCount;

    /**
     * 车票价格
     * 原始字段：车票价格
     * 清洗规则：直接保留，业务核心字段
     * 用途：收入分析、票价分布统计
     */
    private BigDecimal ticketPrice;

    /**
     * 车票类型
     * 原始字段：车票类型
     * 清洗规则：直接保留，业务核心字段
     * 用途：饼图分析（任务书5.4节）、票种结构分析
     */
    private Integer ticketType;

    /**
     * 座位类型编码
     * 原始字段：座位类型编码
     * 清洗规则：直接保留，业务核心字段
     * 用途：座位类型分析、列车编组分析
     */
    private String seatTypeCode;

    /**
     * 列车公司编码
     * 原始字段：列车公司编码
     * 清洗规则：直接保留，业务核心字段
     * 用途：公司维度分析、运营效率评估
     */
    private String trainCompanyCode;

    /**
     * 起点站电报码
     * 原始字段：起点站电报码
     * 清洗规则：直接保留，业务核心字段
     * 用途：车站代码规范校验、电报通信标识
     */
    private String startStationTelecode;

    /**
     * 起点站名称
     * 原始字段：起点站
     * 清洗规则：直接保留，业务核心字段
     * 用途：站点展示、O-D对分析
     */
    private String originStation;

    /**
     * 终到站电报码
     * 原始字段：终点站电报码
     * 清洗规则：直接保留，业务核心字段
     * 用途：车站代码规范校验、电报通信标识
     */
    private String endStationTelecode;

    /**
     * 终到站名称
     * 原始字段：终到站
     * 清洗规则：直接保留，业务核心字段
     * 用途：站点展示、O-D对分析
     */
    private String destStation;

    /**
     * 列车等级码
     * 原始字段：列车等级码
     * 清洗规则：直接保留，业务核心字段
     * 用途：区分高铁/城际/普速，列车等级分析
     */
    private String trainLevelCode;

    /**
     * 列车类型码
     * 原始字段：列车类型码
     * 清洗规则：直接保留，业务核心字段
     * 用途：区分列车类型，车型分布统计
     */
    private String trainTypeCode;

    /**
     * 售票站
     * 原始字段：售票站
     * 清洗规则：直接保留，业务核心字段
     * 用途：售票来源分析、代理商统计
     */
    private String ticketStation;

    /**
     * 最远到达站
     * 原始字段：最远到达站
     * 清洗规则：待关联保留，当前为NULL但保留结构用于后续扩展
     * 用途：支持通票、联程票等复杂票种的后续分析
     */
    private String farthestArrivalStation;

    /**
     * 售票时间
     * 原始字段：售票时间
     * 清洗规则：直接保留，业务核心字段
     * 用途：分析提前购票行为、售票时间分布
     */
    private LocalDate ticketTime;

    /**
     * 到达站
     * 原始字段：到达站
     * 清洗规则：直接保留，业务核心字段
     * 用途：与arriveStationId对应，站点名称展示
     */
    private String arrivalStation;

    /**
     * 数据版本号（清洗批次追溯）
     * 原始字段：无（清洗时生成）
     * 生成规则：v{版本号}_{数据源}_{日期}_{批次号}
     * 示例：v1.1_csv_chengdu_20240101_001
     * 用途：支持数据差错溯源与版本回滚
     */
    private String dataVersion;


}