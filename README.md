# Railway Project - 铁路客流分析与优化系统

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.5.5-blue.svg)](https://baomidou.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 项目简介

Railway Project 是一个基于 Spring Boot 的铁路客流分析与优化系统，提供全面的铁路客运数据分析、可视化展示和智能决策支持功能。系统支持高铁、城际、普速等多种列车类型的客流统计、预测和优化建议。

## ✨ 核心功能

### 1. 数据导入与清洗
- **CSV 数据导入**：支持大规模 CSV 文件异步导入，带进度跟踪
- **智能数据清洗**：自动检测重复数据，执行 4 合 1 合并策略
- **数据质量管理**：标记重复和异常数据，确保数据准确性

### 2. 客流统计分析
- **多维度统计**：支持日/周/月客流统计，环比/同比分析
- **站点客流排行**：TOP 20 繁忙站点和客流站点排名
- **高峰时段分析**：识别最拥挤时段，支持灵敏度配置
- **繁忙指数计算**：综合评估站点繁忙程度

### 3. OD 客流分析
- **OD 热力图**：可视化展示站点间客流分布
- **桑基图数据**：展示客流流向和强度
- **关系图数据**：构建站点网络连接关系
- **矩阵图数据**：提供完整的 OD 客流矩阵

### 4. 线路优化与调度
- **区间满载率分析**：计算各区间每小时满载率
- **过载告警**：连续 7 天上座率超 90% 自动告警
- **增车建议**：基于客流数据生成智能加车建议
- **人工审核流程**：支持建议的审核和管理（待审/通过/拒绝）

### 5. 枢纽识别与分析
- **度中心性分析**：评估站点网络连接度
- **介数中心性分析**：衡量站点在网络中的中介作用
- **枢纽等级评定**：特级至五级枢纽自动分类
- **网别筛选**：支持高铁/城际/普速分别分析

### 6. 城市路线热度
- **路线热度排行**：基于归一化算法避免大城市霸榜
- **热度值计算**：日客流/线路总客流的科学算法

### 7. 站点角色分类
- **始发站识别**：发送比例 ≥ 60%
- **终到站识别**：到达比例 ≥ 60%
- **中转站识别**：中转比例 ≥ 40%
- **通过站识别**：普通站点分类

### 8. 容量匹配度分析
- **站台容量评估**：高峰客流与站台容量匹配度计算
- **容量预警**：识别容量不足的站点

### 9. 客流预测
- **移动平均算法**：基于历史 N 天数据预测
- **周期性分析**：基于历史同期数据预测
- **预测结果管理**：保存和查询预测数据

### 10. 票务组合查询
- **多条件查询**：支持日期范围、车次、站点等组合查询
- **分页支持**：高效处理大数据量查询
- **类型筛选**：车票类型、座位类型、列车类型筛选

## 🛠️ 技术栈

### 后端框架
- **Spring Boot 3.5.4** - 核心框架
- **Java 21** - 编程语言
- **MyBatis Plus 3.5.5** - ORM 框架
- **Spring Data Redis** - 缓存支持

### 数据库
- **MySQL 8.3.0** - 关系型数据库
- **HikariCP** - 高性能连接池

### API 文档
- **Knife4j 4.4.0** - OpenAPI 3.0 文档工具

### 其他依赖
- **Lombok** - 简化 Java 代码
- **FastJSON 2.0.47** - JSON 处理
- **Apache Commons CSV 1.10.0** - CSV 解析
- **JWT (JJWT 0.11.5)** - 身份认证
- **Spring Validation** - 参数校验
- **Spring AOP** - 面向切面编程

## 📁 项目结构

```
railwayProject/
├── src/main/java/com/homework/railwayproject/
│   ├── config/                  # 配置类
│   │   ├── Knife4jConfiguration.java
│   │   ├── MybatisPlusConfiguration.java
│   │   ├── RedisConfig.java
│   │   └── WebMvcConfiguration.java
│   ├── controller/              # 控制器层（23个）
│   │   ├── ImportController.java           # 数据导入
│   │   ├── PassengerFlowStatController.java # 客流统计
│   │   ├── ODHeatMapController.java        # OD热力图
│   │   ├── LineOptimizationController.java # 线路优化
│   │   ├── HubAnalysisController.java      # 枢纽分析
│   │   └── ...
│   ├── service/                 # 服务层
│   │   ├── impl/               # 服务实现
│   │   └── ...
│   ├── mapper/                  # 数据访问层（18个）
│   ├── pojo/                    # 数据对象
│   │   ├── entity/             # 实体类（24个）
│   │   ├── dto/                # 数据传输对象（21个）
│   │   └── vo/                 # 视图对象（4个）
│   ├── task/                    # 定时任务
│   │   ├── BusyIndexStatTask.java
│   │   ├── SectionStatisticsTask.java
│   │   └── StationPassengerFlowStatTask.java
│   ├── exception/               # 异常处理
│   ├── web/                     # Web响应封装
│   └── util/                    # 工具类
├── src/main/resources/
│   ├── mapper/                  # MyBatis XML映射文件
│   ├── application.yml          # 主配置文件
│   └── application-dev.yml      # 开发环境配置
├── docs/                        # API文档
│   ├── ODChartController_API_Document.md
│   ├── ODHeatMapController_API_Document.md
│   ├── SensitivityConfigController_API_Document.md
│   └── 增车建议功能接口文档.md
└── pom.xml                      # Maven配置文件
```

## 🚀 快速开始

### 前置要求

- JDK 21 或更高版本
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 安装步骤

1. **克隆项目**
```bash
git clone https://github.com/your-username/railway-project.git
cd railway-project
```

2. **创建数据库**
```sql
CREATE DATABASE railway_project CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **修改配置**

编辑 `src/main/resources/application-dev.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/railway_project?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

4. **编译项目**
```bash
mvn clean package -DskipTests
```

5. **运行项目**
```bash
java -jar target/railwayProject-0.0.1-SNAPSHOT.jar
```

或者使用 Maven 插件：
```bash
mvn spring-boot:run
```

6. **访问应用**
- 应用地址：http://localhost:9080
- API 文档：http://localhost:9080/doc.html

## 📊 API 接口概览

### 数据导入
- `POST /api/import/high-speed-passenger` - 导入 CSV 数据
- `POST /api/import/high-speed-passenger/async` - 异步导入
- `GET /api/import/progress` - 查询导入进度
- `POST /api/import/high-speed-passenger/clean` - 手动触发数据清洗

### 客流统计
- `GET /api/statistics/daily` - 日客流统计
- `GET /api/statistics/weekly` - 周客流统计
- `GET /api/statistics/monthly` - 月客流统计

### OD 分析
- `GET /api/od-heatmap/by-date` - 获取指定日期 OD 热力图
- `GET /api/od-heatmap/by-date-range` - 获取日期范围 OD 热力图
- `GET /api/od-chart/matrix` - 获取 OD 矩阵数据
- `GET /api/od-chart/sankey` - 获取 OD 桑基图数据
- `GET /api/od-chart/relation` - 获取 OD 关系图数据

### 线路优化
- `GET /api/line-optimization/load-rate/hourly` - 区间每小时满载率
- `GET /api/line-optimization/overload-alerts` - 过载告警
- `GET /api/line-optimization/addition-suggestions` - 增车建议
- `PUT /api/line-optimization/addition-suggestions/{id}/status` - 更新建议状态

### 枢纽分析
- `POST /api/hub/top` - 获取 TOP 枢纽（POST）
- `GET /api/hub/top` - 获取 TOP 枢纽（GET）

### 站点管理
- `GET /api/station/page` - 分页查询站点
- `GET /api/station/levelStat` - 站点等级统计
- `GET /api/station-passenger-flow-stat/top20` - 站点客流 TOP 20

### 高峰时段
- `GET /api/peak-hour-stat/peak-hours` - 获取高峰时段
- `GET /api/peak-hour-stat/top3-consecutive-peaks` - 最拥挤三连续时段

### 繁忙指数
- `GET /api/busy-index-stat/top20` - 繁忙指数前 20 名

### 城市路线热度
- `GET /api/city-route-heat/by-date` - 城市路线热度排行

### 站点角色
- `GET /api/station-role/classify` - 站点角色分类

### 容量匹配
- `GET /api/capacity-matching/calculate-by-site` - 容量匹配度计算

### 客流预测
- `GET /api/passenger-flow-prediction/predict-by-moving-average` - 移动平均预测
- `GET /api/passenger-flow-prediction/predict-by-periodicity` - 周期性预测

### 票务查询
- `POST /api/ticket/query` - 组合查询票务数据
- `GET /api/ticket/ticket-types` - 车票类型列表
- `GET /api/ticket/seat-types` - 座位类型列表

### 线路断面地图
- `GET /api/line-section-map/load-rate-map` - 线路断面满载率地图

详细 API 文档请查看 [docs](docs/) 目录或访问 Knife4j 在线文档。

## 🔧 配置说明

### 主要配置项

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| server.port | 9080 | 服务端口 |
| spring.datasource.url | jdbc:mysql://localhost:3306/railway_project | 数据库连接 |
| spring.data.redis.host | localhost | Redis 主机 |
| spring.data.redis.port | 6379 | Redis 端口 |
| mybatis-plus.mapper-locations | classpath*:/mapper/**/*.xml | Mapper 文件位置 |
| knife4j.basic.enable | true | 启用 API 文档 |

### 数据版本
当前数据版本：`1.1`（在 `application.yml` 中配置）

## 📝 数据库设计

系统包含以下核心数据表：
- `high_speed_passenger` - 原始客运数据
- `high_speed_passenger_clean` - 清洗后数据
- `station` - 站点信息
- `line` - 线路信息
- `train` - 列车信息
- `line_station` - 线路站点关系
- `passenger_flow_stat` - 客流统计
- `od_heat_map` - OD 热力图数据
- `busy_index_stat` - 繁忙指数统计
- `peak_hour_stat` - 高峰时段统计
- `hub_analysis` - 枢纽分析结果
- `train_addition_suggestion` - 增车建议
- `sensitivity_config` - 灵敏度配置
- `capacity_matching_stat` - 容量匹配度
- `passenger_flow_prediction` - 客流预测
- 等等...

详细的数据库脚本请参考 `src/main/java/com/homework/railwayproject/util/` 目录下的 SQL 文件。

## 🎯 特色功能

### 1. 异步导入与进度跟踪
支持大文件异步导入，实时返回处理进度，提升用户体验。

### 2. 智能数据清洗
自动检测重复数据（状态标记为 3），执行 4 合 1 合并策略，保证数据质量。

### 3. 缓存优化
满载率等计算密集型数据缓存 2 小时，显著提升查询性能。

### 4. 灵活的灵敏度配置
支持动态调整高峰时段识别阈值，适应不同场景需求。

### 5. 科学的枢纽评级算法
采用 `ln(度中心性+1)*10 + 介数中心性*1000` 公式，综合评估站点重要性。

### 6. 归一化热度算法
使用 `日客流/线路总客流` 计算热度值，避免大城市霸榜问题。

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 👨‍💻 作者

**谢云轩**
- QQ: 1721476339
- Email: 1721476339@qq.com

## 🙏 致谢

感谢以下开源项目：
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis Plus](https://baomidou.com/)
- [Knife4j](https://doc.xiaominfo.com/)
- [MySQL](https://www.mysql.com/)
- [Redis](https://redis.io/)

## 📞 联系方式

如有问题或建议，请通过以下方式联系：
- 提交 Issue
- 发送邮件至 1721476339@qq.com

---

⭐ 如果这个项目对您有帮助，请给个 Star！
