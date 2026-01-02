# 线路断面满载率地图接口文档

## 接口概览

### 基本信息
- **接口前缀**: `/api/line-section-map`
- **功能描述**: 提供线路断面满载率数据，用于前端地图可视化展示
- **技术架构**: Spring Boot + MyBatis + Redis缓存

## 接口列表

### 1. 获取线路断面满载率地图数据

#### 接口信息
- **接口路径**: `/api/line-section-map/load-rate-map`
- **请求方法**: `GET`
- **功能描述**: 获取各线路断面的满载率分布数据，用于地图可视化展示

#### 请求参数

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| lineCode | String | 否 | - | 线路编码，用于筛选特定线路 |
| flowDate | String | 否 | - | 统计日期，格式：yyyy-MM-dd |
| startDate | String | 否 | - | 开始日期，格式：yyyy-MM-dd，与endDate配合使用 |
| endDate | String | 否 | - | 结束日期，格式：yyyy-MM-dd，与startDate配合使用 |
| startHour | Integer | 否 | - | 开始小时，0-23 |
| endHour | Integer | 否 | - | 结束小时，0-23 |
| startStationId | Integer | 否 | - | 起始站ID，优先级高于startStationName |
| endStationId | Integer | 否 | - | 终点站ID，优先级高于endStationName |
| startStationName | String | 否 | - | 起始站名称，当未指定startStationId时使用 |
| endStationName | String | 否 | - | 终点站名称，当未指定endStationId时使用 |
| page | Integer | 否 | 1 | 页码，从1开始 |
| size | Integer | 否 | 10 | 每页大小 |

#### 请求示例

```bash
# 查询特定线路的满载率数据
GET /api/line-section-map/load-rate-map?lineCode=LINE001&page=1&size=20

# 查询特定日期范围的满载率数据
GET /api/line-section-map/load-rate-map?startDate=2024-01-01&endDate=2024-01-31&startHour=8&endHour=18

# 查询特定站点间的满载率数据
GET /api/line-section-map/load-rate-map?startStationId=100&endStationId=200
```

#### 响应数据结构

```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "lineCode": "LINE001",
        "lineName": "1号线",
        "startStationId": 100,
        "startStationName": "天安门东",
        "startLongitude": 116.4074,
        "startLatitude": 39.9163,
        "endStationId": 101,
        "endStationName": "天安门西",
        "endLongitude": 116.3972,
        "endLatitude": 39.9181,
        "flowDate": "2024-01-15",
        "hour": 8,
        "timeRange": "08:00-08:59",
        "passengerCount": 1250,
        "trainCapacity": 1500,
        "loadRate": 83.33,
        "lineType": "地铁"
      }
    ],
    "total": 150,
    "size": 10,
    "current": 1,
    "pages": 15
  },
  "message": "请求成功"
}
```

#### 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应状态码，200表示成功 |
| data | Object | 分页数据对象 |
| data.records | Array | 当前页数据列表 |
| data.total | Long | 总记录数 |
| data.size | Integer | 每页大小 |
| data.current | Long | 当前页码 |
| data.pages | Long | 总页数 |
| message | String | 响应消息 |

##### 记录字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| lineCode | String | 线路编码 |
| lineName | String | 线路名称 |
| startStationId | Integer | 起始站ID |
| startStationName | String | 起始站名称 |
| startLongitude | BigDecimal | 起始站经度 |
| startLatitude | BigDecimal | 起始站纬度 |
| endStationId | Integer | 终点站ID |
| endStationName | String | 终点站名称 |
| endLongitude | BigDecimal | 终点站经度 |
| endLatitude | BigDecimal | 终点站纬度 |
| flowDate | LocalDate | 统计日期 |
| hour | Integer | 小时（0-23） |
| timeRange | String | 时间范围显示（如：08:00-08:59） |
| passengerCount | Integer | 客流量 |
| trainCapacity | Integer | 列车总运力 |
| loadRate | Double | 满载率（百分比） |
| lineType | String | 线路类型 |

#### 错误码说明

| 错误码 | 说明 | 可能原因 |
|--------|------|----------|
| 200 | 请求成功 | - |
| 500 | 服务器内部错误 | 系统异常 |

### 2. 获取所有线路信息

#### 接口信息
- **接口路径**: `/api/line-section-map/lines`
- **请求方法**: `GET`
- **功能描述**: 获取所有线路的基本信息，用于地图界面的线路选择

#### 请求示例

```bash
GET /api/line-section-map/lines
```

#### 响应数据结构

```json
{
  "code": 200,
  "data": [
    {
      "lineCode": "LINE001",
      "lineName": "1号线",
      "lineType": "地铁"
    },
    {
      "lineCode": "LINE002",
      "lineName": "2号线",
      "lineType": "地铁"
    }
  ],
  "message": "请求成功"
}
```

#### 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应状态码，200表示成功 |
| data | Array | 线路信息列表 |
| message | String | 响应消息 |

##### 线路信息字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| lineCode | String | 线路编码 |
| lineName | String | 线路名称 |
| lineType | String | 线路类型 |

## 使用说明

### 1. 基本使用场景

#### 场景一：查询特定线路的满载率数据
```bash
GET /api/line-section-map/load-rate-map?lineCode=LINE001
```

#### 场景二：查询特定时间段的满载率数据
```bash
GET /api/line-section-map/load-rate-map?startDate=2024-01-01&endDate=2024-01-31&startHour=8&endHour=18
```

#### 场景三：查询特定站点间的满载率数据
```bash
GET /api/line-section-map/load-rate-map?startStationId=100&endStationId=200
```

### 2. 参数组合说明

- 日期参数：[flowDate](file://D:\railwayProject\src\main\java\com\homework\railwayproject\pojo\vo\LoadRateVO.java#L13-L13) 与 [startDate](file://D:\railwayProject\src\main\java\com\homework\railwayproject\service\impl\StationPassengerFlowStatServiceImpl.java#L32-L32)/[endDate](file://D:\railwayProject\src\main\java\com\homework\railwayproject\service\impl\StationPassengerFlowStatServiceImpl.java#L33-L33) 不能同时使用，[flowDate](file://D:\railwayProject\src\main\java\com\homework\railwayproject\pojo\vo\LoadRateVO.java#L13-L13) 优先级更高
- 站点参数：站点ID参数优先级高于站点名称参数
- 时间参数：[startHour](file://D:\railwayProject\src\main\java\com\homework\railwayproject\pojo\dto\LineSectionLoadRateQueryDTO.java#L12-L12) 和 [endHour](file://D:\railwayProject\src\main\java\com\homework\railwayproject\pojo\dto\LineSectionLoadRateQueryDTO.java#L13-L13) 需要成对使用才有效果

### 3. 缓存机制

- 所有查询结果都会缓存到Redis中，缓存时间为2小时
- 缓存键基于所有查询参数生成，确保不同参数组合的数据独立缓存
- 缓存键格式：`line_section_load_rate_map:lineCode:flowDate:startDate:endDate:startHour:endHour:startStationId:endStationId:startStationName:endStationName:page[size]`

### 4. 性能优化

- 采用分页查询，避免大数据量一次性加载
- 支持按条件筛选，减少不必要的数据传输
- 使用Redis缓存，提升查询性能

## 注意事项

1. 所有日期参数格式必须为 `yyyy-MM-dd`
2. 小时参数范围为 0-23
3. 站点ID参数优先级高于站点名称参数
4. 满载率计算公式：(乘客数量 * 100.0 / 列车运力)
5. 只返回乘客数量大于0的记录
6. 缓存时间设置为2小时，数据更新后需要等待缓存过期或清除缓存
7. 默认分页大小为10条记录，可根据需要调整