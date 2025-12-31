# OD热力图控制器接口文档

## 概述
OD热力图控制器提供OD（起始点-目的地）客流热力图数据接口，支持按日期、日期范围、站点等维度查询数据。

## 接口列表

### 1. 获取指定日期的OD热力图数据

#### 接口信息
- **接口路径**: `GET /api/od-heatmap/by-date`
- **功能描述**: 返回指定日期的起始点-目的地客流数据

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| date | Date | 是 | 指定日期 | 2024-01-01 |

#### 返回数据
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "originStationId": 1,
      "originStationName": "北京南站",
      "destStationId": 2,
      "destStationName": "上海虹桥",
      "passengerFlow": 1500,
      "travelDate": "2024-01-01",
      "heatValue": 0.2500,
      "rank": 1
    },
    {
      "originStationId": 2,
      "originStationName": "上海虹桥",
      "destStationId": 3,
      "destStationName": "广州南站",
      "passengerFlow": 1200,
      "travelDate": "2024-01-01",
      "heatValue": 0.2000,
      "rank": 2
    }
  ]
}
```

#### 参数说明
- `originStationId`: 起始站点ID
- `originStationName`: 起始站点名称
- `destStationId`: 目标站点ID
- `destStationName`: 目标站点名称
- `passengerFlow`: 客流量（上客量+下客量）
- `travelDate`: 乘车日期
- `heatValue`: 热力值（该OD对客流量/总客流量）
- `rank`: 排名（按客流量排序）

---

### 2. 获取日期范围的OD热力图数据

#### 接口信息
- **接口路径**: `GET /api/od-heatmap/by-date-range`
- **功能描述**: 返回指定日期范围内的起始点-目的地客流数据

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| startDate | Date | 是 | 开始日期 | 2024-01-01 |
| endDate | Date | 是 | 结束日期 | 2024-01-07 |

#### 返回数据
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "originStationId": 1,
      "originStationName": "北京南站",
      "destStationId": 2,
      "destStationName": "上海虹桥",
      "passengerFlow": 10500,
      "travelDate": "2024-01-01 to 2024-01-07",
      "heatValue": 0.2500,
      "rank": 1
    }
  ]
}
```

#### 参数说明
- 参数定义同上，travelDate表示日期范围

---

### 3. 获取指定出发站点的OD热力图数据

#### 接口信息
- **接口路径**: `GET /api/od-heatmap/by-departure-station`
- **功能描述**: 返回从指定站点出发的客流热力图数据

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| stationId | Integer | 是 | 站点ID | 1 |
| date | Date | 是 | 指定日期 | 2024-01-01 |

#### 返回数据
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "originStationId": 1,
      "originStationName": "北京南站",
      "destStationId": 2,
      "destStationName": "上海虹桥",
      "passengerFlow": 1500,
      "travelDate": "2024-01-01",
      "heatValue": 0.5000,
      "rank": 1
    }
  ]
}
```

---

### 4. 获取指定到达站点的OD热力图数据

#### 接口信息
- **接口路径**: `GET /api/od-heatmap/by-arrival-station`
- **功能描述**: 返回到达指定站点的客流热力图数据

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| stationId | Integer | 是 | 站点ID | 2 |
| date | Date | 是 | 指定日期 | 2024-01-01 |

#### 返回数据
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "originStationId": 1,
      "originStationName": "北京南站",
      "destStationId": 2,
      "destStationName": "上海虹桥",
      "passengerFlow": 1500,
      "travelDate": "2024-01-01",
      "heatValue": 0.6000,
      "rank": 1
    }
  ]
}
```

---

### 5. 获取指定日期的OD热力图数据（DTO格式）

#### 接口信息
- **接口路径**: `GET /api/od-heatmap/dto-by-date`
- **功能描述**: 返回适合前端展示的OD热力图数据

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| date | Date | 是 | 指定日期 | 2024-01-01 |

#### 返回数据
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "odData": [
      {
        "originStation": {
          "stationId": 1,
          "stationName": "北京南站"
        },
        "destStation": {
          "stationId": 2,
          "stationName": "上海虹桥"
        },
        "passengerFlow": 1500,
        "dateRange": "2024-01-01",
        "heatValue": 0.2500
      }
    ],
    "dateRange": "2024-01-01",
    "totalRecords": 1
  }
}
```

#### 参数说明
- `odData`: OD数据列表
- `dateRange`: 日期范围
- `totalRecords`: 总记录数

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 500 | 服务器内部错误 |

## 使用建议

1. **日期参数格式**: 日期参数必须使用 `yyyy-MM-dd` 格式
2. **数据存在性**: 确保查询的日期在数据库中存在对应的客流数据
3. **性能优化**: 对于大量数据的查询，建议添加适当的缓存机制
4. **异常处理**: 前端应处理可能的网络异常和数据格式错误

## 前端开发建议

### 1. 图表库选择
- **ECharts**: 适合热力图表格展示，支持矩阵热力图
- **D3.js**: 适合复杂的数据可视化需求
- **AntV G6**: 专注于关系图和流程图的展示

### 2. 数据处理
- 对返回的热力值进行分级处理，设置不同的颜色区间
- 根据客流量大小调整图表元素的样式（颜色深浅、大小等）
- 对于大矩阵，考虑分页或缩放功能

### 3. 用户交互
- 添加时间选择器，支持不同日期的OD数据对比
- 提供筛选功能，支持按站点或客流量范围筛选
- 实现热力图的交互功能，如悬停显示详细信息