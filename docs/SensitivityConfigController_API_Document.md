# 灵敏度配置接口文档

## 接口说明

灵敏度配置接口用于管理系统中各类统计分析的灵敏度参数，如高峰时段统计灵敏度、繁忙指数灵敏度等。

## 接口详情

### 1. 根据配置类型获取灵敏度配置

#### 接口地址
```
GET /api/sensitivity-config/get-by-type
```

#### 请求参数
| 参数名 | 参数类型 | 必填 | 默认值 | 说明 |
|--------|----------|------|--------|------|
| configType | String | 是 | - | 配置类型，如 peak_hour_sensitivity, busy_index_sensitivity 等 |

#### 请求示例
```
GET /api/sensitivity-config/get-by-type?configType=peak_hour_sensitivity
```

#### 响应数据结构
```json
{
  "state": 20000,
  "message": null,
  "data": {
    "id": 1,
    "configName": "高峰时段统计灵敏度",
    "configType": "peak_hour_sensitivity",
    "sensitivityValue": 0.15,
    "description": "用于高峰时段统计的灵敏度配置",
    "createTime": "2025-12-30 10:30:00",
    "updateTime": "2025-12-30 10:30:00",
    "createUser": null,
    "updateUser": null,
    "isDeleted": 0
  }
}
```

#### 响应参数说明
| 参数名 | 类型 | 说明 |
|--------|------|------|
| state | Integer | 状态码，20000表示成功 |
| message | String | 消息，成功时为null |
| data | Object | 灵敏度配置对象 |

#### data 参数说明
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Integer | 配置主键ID |
| configName | String | 配置名称 |
| configType | String | 配置类型 |
| sensitivityValue | Double | 灵敏度值（0.0-1.0之间） |
| description | String | 配置描述 |
| createTime | String | 创建时间 |
| updateTime | String | 更新时间 |
| createUser | String | 创建用户 |
| updateUser | String | 更新用户 |
| isDeleted | Integer | 是否已删除（0表示未删除） |

---

### 2. 根据配置类型更新灵敏度值

#### 接口地址
```
POST /api/sensitivity-config/update-by-type
```

#### 请求参数
| 参数名 | 参数类型 | 必填 | 默认值 | 说明 |
|--------|----------|------|--------|------|
| configType | String | 是 | - | 配置类型，如 peak_hour_sensitivity, busy_index_sensitivity 等 |
| sensitivityValue | Double | 是 | - | 灵敏度值，范围为 0.0-1.0 |

#### 请求示例
```
POST /api/sensitivity-config/update-by-type?configType=peak_hour_sensitivity&sensitivityValue=0.2
Content-Type: application/x-www-form-urlencoded
```

#### 响应数据结构
```json
{
  "state": 20000,
  "message": null,
  "data": true
}
```

#### 响应参数说明
| 参数名 | 类型 | 说明 |
|--------|------|------|
| state | Integer | 状态码，20000表示成功，50200表示更新失败 |
| message | String | 消息，成功时为null，失败时为错误信息 |
| data | Boolean | 更新结果，true表示成功，false表示失败 |

---

### 3. 获取高峰时段统计的灵敏度配置

#### 接口地址
```
GET /api/sensitivity-config/peak-hour
```

#### 请求参数
无

#### 请求示例
```
GET /api/sensitivity-config/peak-hour
```

#### 响应数据结构
```json
{
  "state": 20000,
  "message": null,
  "data": 0.15
}
```

#### 响应参数说明
| 参数名 | 类型 | 说明 |
|--------|------|------|
| state | Integer | 状态码，20000表示成功 |
| message | String | 消息，成功时为null |
| data | Double | 高峰时段统计的灵敏度值（0.0-1.0之间） |

---

### 4. 更新高峰时段统计的灵敏度配置

#### 接口地址
```
POST /api/sensitivity-config/peak-hour
```

#### 请求参数
| 参数名 | 参数类型 | 必填 | 默认值 | 说明 |
|--------|----------|------|--------|------|
| sensitivityValue | Double | 是 | - | 灵敏度值，范围为 0.0-1.0 |

#### 请求示例
```
POST /api/sensitivity-config/peak-hour?sensitivityValue=0.2
Content-Type: application/x-www-form-urlencoded
```

#### 响应数据结构
```json
{
  "state": 20000,
  "message": null,
  "data": true
}
```

#### 响应参数说明
| 参数名 | 类型 | 说明 |
|--------|------|------|
| state | Integer | 状态码，20000表示成功，50200表示更新失败 |
| message | String | 消息，成功时为null，失败时为错误信息 |
| data | Boolean | 更新结果，true表示成功，false表示失败 |

## 常见错误码

| 错误码 | 说明 |
|--------|------|
| 20000 | 操作成功 |
| 50200 | 更新失败 |
| 40000 | 请求参数格式错误 |

## 注意事项

1. 灵敏度值必须在 0.0 到 1.0 的范围内，否则更新操作会失败
2. configType 参数不能为空
3. 所有请求参数必须按照指定格式传递
4. 建议在生产环境中使用 HTTPS 协议访问接口