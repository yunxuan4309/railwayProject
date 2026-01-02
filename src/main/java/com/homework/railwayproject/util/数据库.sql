create table high_speed_passenger
(
    id                       bigint                               not null comment '主键ID'
        primary key,
    line_code                varchar(50)                          null comment '运营线路编码',
    train_code               int                                  null comment '列车编码',
    site_id                  int                                  null comment '站点id',
    line_site_id             int                                  null comment '线路站点id',
    upline_code              varchar(50)                          null comment '上行线编码',
    operation_date           date                                 null comment '运行日期',
    operation_time           varchar(10)                          null comment '运行时间（原始格式，如"749"）',
    distance_order           int                                  null comment '与起点站距序',
    is_start_site            tinyint(1)                           null comment '是否起始站点：0-否，1-是',
    is_end_site              tinyint(1)                           null comment '是否终点站点：0-否，1-是',
    arrival_time             varchar(10)                          null comment '到达时间',
    departure_time           varchar(10)                          null comment '出发时间',
    stop_time                varchar(10)                          null comment '经停时间',
    passenger_flow           int                                  null comment '客流量',
    upline_passenger_flow    int                                  null comment '上行客流量',
    downline_passenger_flow  int                                  null comment '下行客流量',
    boarding_count           int                                  null comment '上客量',
    alighting_count          int                                  null comment '下客量',
    remarks                  varchar(500)                         null comment '备注',
    train_departure_date     date                                 null comment '列车出发日期',
    train_departure_time     varchar(10)                          null comment '列车出发时间',
    site_sequence            int                                  null comment '站点序号',
    ticket_type              int                                  null comment '车票类型',
    ticket_price             decimal(10, 2)                       null comment '车票价格',
    seat_type_code           varchar(20)                          null comment '座位类型编码',
    train_company_code       varchar(50)                          null comment '列车公司编码',
    start_date               date                                 null comment '开始日期',
    start_station_telecode   varchar(20)                          null comment '起点站电报码',
    start_station            varchar(100)                         null comment '起点站名称',
    end_station_telecode     varchar(20)                          null comment '终到站电报码',
    end_station              varchar(100)                         null comment '终到站名称',
    train_level_code         varchar(20)                          null comment '列车等级码',
    train_type_code          varchar(20)                          null comment '列车类型码',
    ticket_station           varchar(100)                         null comment '售票站',
    farthest_arrival_station varchar(100)                         null comment '最远到达站',
    ticket_time              date                                 null comment '售票时间',
    arrival_station          varchar(100)                         null comment '到达站',
    revenue                  decimal(10, 2)                       null comment '收入（元）',
    create_time              datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time              datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by                varchar(50)                          null comment '创建人',
    update_by                varchar(50)                          null comment '更新人',
    status                   int        default 1                 null comment '数据状态：1-有效，0-无效',
    is_deleted               tinyint(1) default 0                 null comment '是否删除：0-未删除，1-已删除'
)
    comment '高铁客运原始数据导入表（未清洗）';

create index idx_line_code
    on high_speed_passenger (line_code);

create index idx_operation_date
    on high_speed_passenger (operation_date);

create index idx_site_id
    on high_speed_passenger (site_id);

create index idx_train_code
    on high_speed_passenger (train_code);

create table high_speed_passenger_clean
(
    id                       bigint auto_increment comment '主键ID'
        primary key,
    ticket_id                varchar(100)                         not null comment '车票唯一ID（清洗后生成）格式：列车编码_运行日期_归一化序列',
    operation_line_code      varchar(50)                          null comment '运营线路编码',
    train_code               int                                  not null comment '列车编码',
    original_site_id         int                                  null comment '原始站点ID（数据验证专用）',
    depart_station_id        int                                  null comment '上车站点ID（清洗推导）',
    arrive_station_id        int                                  null comment '下车站点ID（清洗推导）',
    line_site_id             int                                  null comment '线路站点id',
    upline_code              varchar(50)                          null comment '上行线编码',
    travel_date              date                                 not null comment '乘车日期',
    depart_time              time                                 null comment '出发时间（格式转换后，如"07:49:00"）',
    distance_seq             int                                  null comment '与起点站距序',
    boarding_count           int                                  null comment '上客量',
    alighting_count          int                                  null comment '下客量',
    ticket_price             decimal(10, 2)                       null comment '车票价格（元）',
    ticket_type              int                                  null comment '车票类型',
    seat_type_code           varchar(20)                          null comment '座位类型编码',
    train_company_code       varchar(50)                          null comment '列车公司编码',
    start_station_telecode   varchar(20)                          null comment '起点站电报码',
    origin_station           varchar(100)                         null comment '起点站名称',
    end_station_telecode     varchar(20)                          null comment '终到站电报码',
    dest_station             varchar(100)                         null comment '终到站名称',
    train_level_code         varchar(20)                          null comment '列车等级码',
    train_type_code          varchar(20)                          null comment '列车类型码',
    ticket_station           varchar(100)                         null comment '售票站',
    farthest_arrival_station varchar(100)                         null comment '最远到达站',
    ticket_time              date                                 null comment '售票时间',
    arrival_station          varchar(100)                         null comment '到达站',
    data_version             varchar(50)                          null comment '数据版本号（清洗批次追溯）格式：v1.1_csv_chengdu_20240101_001',
    create_time              datetime   default CURRENT_TIMESTAMP null comment '创建时间（清洗入库时间）',
    update_time              datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by                varchar(50)                          null comment '创建人',
    update_by                varchar(50)                          null comment '更新人',
    status                   int        default 1                 null comment '数据状态：1-有效，0-无效',
    is_deleted               tinyint(1) default 0                 null comment '是否删除：0-未删除，1-已删除',
    constraint uk_ticket_id
        unique (ticket_id),
    constraint fk_clean_arrive_station
        foreign key (arrive_station_id) references station (site_id),
    constraint fk_clean_depart_station
        foreign key (depart_station_id) references station (site_id),
    constraint fk_clean_train
        foreign key (train_code) references train (train_code)
)
    comment '高铁客运清洗数据表（合并4条原始记录为1条有效车票数据）';

create index idx_arrive_station
    on high_speed_passenger_clean (arrive_station_id);

create index idx_depart_station
    on high_speed_passenger_clean (depart_station_id);

create index idx_ticket_type
    on high_speed_passenger_clean (ticket_type);

create index idx_train_code
    on high_speed_passenger_clean (train_code);

create index idx_travel_date
    on high_speed_passenger_clean (travel_date);

create index idx_original_site_id
    on high_speed_passenger_clean (original_site_id);
create table line
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    line_code        varchar(50)                          not null comment '线路编码',
    line_name        varchar(100)                         null comment '线路名称',
    line_type        varchar(20)                          null comment '线路类型（高铁/城际/普速）',
    start_station_id int                                  null comment '起始站id',
    end_station_id   int                                  null comment '终点站id',
    total_mileage    decimal(10, 2)                       null comment '总里程（公里）',
    operation_status varchar(20)                          null comment '运营状态（运营中/在建/停运）',
    create_time      datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time      datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by        varchar(50)                          null comment '创建人',
    update_by        varchar(50)                          null comment '更新人',
    status           int        default 1                 null comment '数据状态：1-有效，0-无效',
    is_deleted       tinyint(1) default 0                 null comment '是否删除：0-未删除，1-已删除',
    constraint uk_line_code
        unique (line_code),
    constraint fk_line_end_station
        foreign key (end_station_id) references station (site_id),
    constraint fk_line_start_station
        foreign key (start_station_id) references station (site_id)
)
    comment '线路信息表';

create index idx_end_station
    on line (end_station_id);

create index idx_line_name
    on line (line_name);

create index idx_start_station
    on line (start_station_id);
create table line_station
(
    id                 bigint auto_increment comment '主键ID'
        primary key,
    line_code          varchar(50)                          not null comment '运营线路编码',
    site_id            int                                  not null comment '站点id',
    line_site_id       int                                  null comment '线路站点id（线路内序号）',
    previous_site_id   int                                  null comment '上一站id',
    line_distance      decimal(10, 2)                       null comment '运营线路站间距离（公里）',
    next_site_id       int                                  null comment '下一站id',
    is_start_station   tinyint(1) default 0                 null comment '是否起始站点：0-否，1-是',
    is_end_station     tinyint(1) default 0                 null comment '是否终点站点：0-否，1-是',
    transport_distance decimal(10, 2)                       null comment '运输距离（公里）',
    line_number        varchar(50)                          null comment '线路代码',
    need_stop          tinyint(1) default 1                 null comment '是否要停靠：0-不停靠，1-停靠',
    create_time        datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time        datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by          varchar(50)                          null comment '创建人',
    update_by          varchar(50)                          null comment '更新人',
    status             int        default 1                 null comment '数据状态：1-有效，0-无效',
    is_deleted         tinyint(1) default 0                 null comment '是否删除：0-未删除，1-已删除',
    constraint uk_line_site
        unique (line_code, site_id),
    constraint fk_line_station_line
        foreign key (line_code) references line (line_code),
    constraint fk_line_station_station
        foreign key (site_id) references station (site_id)
)
    comment '线路站点关系表（记录站点在线路上的顺序和距离）';

create index idx_line_number
    on line_station (line_number);

create index idx_site_id
    on line_station (site_id);

create table station
(
    id                  bigint auto_increment comment '主键ID'
        primary key,
    site_id             int                                  not null comment '站点ID',
    type_id             int                                  null comment '类型ID',
    transport_mode_code int                                  null comment '运输方式编码',
    station_name        varchar(100)                         not null comment '站点名称',
    is_disabled         tinyint(1) default 0                 null comment '是否停用：0-启用，1-停用',
    site_code           varchar(50)                          null comment '站点code',
    station_telecode    varchar(20)                          null comment '站点电报码',
    station_alias       varchar(100)                         null comment '站点小名',
    longitude           decimal(10, 7)                       null comment '经度',
    latitude            decimal(10, 7)                       null comment '纬度',
    station_level       varchar(10)                          null comment '站点等级',
    platform_count      int                                  null comment '站台数量',
    gate_count          int                                  null comment '检票口数量',
    city                varchar(50)                          null comment '所属城市',
    create_time         datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time         datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by           varchar(50)                          null comment '创建人',
    update_by           varchar(50)                          null comment '更新人',
    status              int        default 1                 null comment '数据状态：1-有效，0-无效',
    is_deleted          tinyint(1) default 0                 null comment '是否删除：0-未删除，1-已删除',
    constraint uk_site_id
        unique (site_id)
)
    comment '站点信息表';

create index idx_city
    on station (city);

create index idx_station_name
    on station (station_name);

create index idx_telecode
    on station (station_telecode);

create table train
(
    id                  bigint auto_increment comment '主键ID'
        primary key,
    train_code          int                                  not null comment '列车编码',
    upline_code         varchar(50)                          null comment '上行线编码',
    transport_mode_code int                                  null comment '运输方式编码',
    train_number        varchar(20)                          null comment '列车代码',
    train_id            varchar(20)                          null comment '车次（如G1234）',
    is_official         tinyint(1) default 1                 null comment '是否正图：0-非正图，1-正图',
    train_capacity      int                                  null comment '列车运量（座位数）',
    create_time         datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time         datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by           varchar(50)                          null comment '创建人',
    update_by           varchar(50)                          null comment '更新人',
    status              int        default 1                 null comment '数据状态：1-有效，0-无效',
    is_deleted          tinyint(1) default 0                 null comment '是否删除：0-未删除，1-已删除',
    constraint uk_train_code
        unique (train_code)
)
    comment '列车信息表';

create index idx_train_id
    on train (train_id);

create index idx_upline_code
    on train (upline_code);

-- 创建灵敏度配置表
CREATE TABLE sensitivity_config (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    config_name VARCHAR(100) NOT NULL COMMENT '配置名称',
    config_type VARCHAR(50) NOT NULL UNIQUE COMMENT '配置类型',
    sensitivity_value DOUBLE(10,2) DEFAULT 0.15 COMMENT '数值（对于容量配置为整数值，对于灵敏度配置为0.00-1.00之间的值）',
    description VARCHAR(255) COMMENT '配置描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除，0-未删除，1-已删除'
) COMMENT='灵敏度配置表';

-- 插入默认的高峰时段灵敏度配置
INSERT INTO sensitivity_config (config_name, config_type, sensitivity_value, description) 
VALUES ('高峰时段统计灵敏度', 'peak_hour_sensitivity', 0.15, '用于高峰时段统计算法的灵敏度参数，该配置可用于繁忙指数的权重配置和高峰时段灵敏度配置，范围均在0到1之间');
-- 插入满载率告警阈值配置
INSERT INTO sensitivity_config (config_name, config_type, sensitivity_value, description)
VALUES ('满载率告警阈值', 'load_factor_threshold', 0.7, '列车满载率告警阈值，超过此值将触发告警，范围0到1之间');

-- 插入繁忙指数权重 - 发送量配置
INSERT INTO sensitivity_config (config_name, config_type, sensitivity_value, description)
VALUES ('繁忙指数权重 - 发送量', 'busy_index_departure_weight', 0.4, '繁忙指数计算中发送量的权重，范围0到1之间');

-- 插入繁忙指数权重 - 到达量配置
INSERT INTO sensitivity_config (config_name, config_type, sensitivity_value, description)
VALUES ('繁忙指数权重 - 到达量', 'busy_index_arrival_weight', 0.6, '繁忙指数计算中到达量的权重，范围0到1之间');

-- 插入繁忙指数权重 - 中转量配置
INSERT INTO sensitivity_config (config_name, config_type, sensitivity_value, description)
VALUES ('繁忙指数权重 - 中转量', 'busy_index_transfer_weight', 0.0, '繁忙指数计算中中转量的权重，范围0到1之间');

-- 为high_speed_passenger_clean表添加复合索引
CREATE INDEX idx_travel_date_time_site ON high_speed_passenger_clean(travel_date, depart_time, original_site_id);
-- 添加站点等级容量配置到sensitivity_config表
-- 配置格式：description字段存储"站台容量,检票口容量"



-- 以下数据库表格不使用
-- ==================== 线路优化接口所需表（）====================

-- 1. 创建区间每小时客流统计表（用于缓存计算结果）
CREATE TABLE IF NOT EXISTS section_hourly_flow (
                                                   id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                                   line_code VARCHAR(50) NOT NULL COMMENT '线路编码',
    start_station_id INT NOT NULL COMMENT '起始站ID',
    end_station_id INT NOT NULL COMMENT '终点站ID',
    flow_date DATE NOT NULL COMMENT '统计日期',
    hour INT NOT NULL COMMENT '小时 (0-23)',
    passenger_count INT NOT NULL COMMENT '客流量',
    train_capacity INT NOT NULL COMMENT '列车总运力',
    load_rate DECIMAL(5, 2) NOT NULL COMMENT '满载率 (%)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    UNIQUE KEY uk_section_hour (line_code, start_station_id, end_station_id, flow_date, hour)
    ) COMMENT '区间每小时客流统计表';

-- 2. 创建区间每日客流统计表（用于7天连续判断）
CREATE TABLE IF NOT EXISTS section_daily_flow (
                                                  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                                  line_code VARCHAR(50) NOT NULL COMMENT '线路编码',
    start_station_id INT NOT NULL COMMENT '起始站ID',
    end_station_id INT NOT NULL COMMENT '终点站ID',
    flow_date DATE NOT NULL COMMENT '统计日期',
    avg_load_rate DECIMAL(5, 2) NOT NULL COMMENT '平均满载率 (%)',
    max_load_rate DECIMAL(5, 2) NOT NULL COMMENT '最高满载率 (%)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    UNIQUE KEY uk_section_date (line_code, start_station_id, end_station_id, flow_date)
    ) COMMENT '区间每日客流统计表';

-- 3. 创建过载告警记录表
CREATE TABLE IF NOT EXISTS overload_alert (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                              line_code VARCHAR(50) NOT NULL COMMENT '线路编码',
    start_station_id INT NOT NULL COMMENT '起始站ID',
    end_station_id INT NOT NULL COMMENT '终点站ID',
    alert_start_date DATE NOT NULL COMMENT '告警开始日期',
    alert_end_date DATE NOT NULL COMMENT '告警结束日期',
    consecutive_days INT NOT NULL COMMENT '连续天数',
    avg_load_rate DECIMAL(5, 2) NOT NULL COMMENT '平均满载率 (%)',
    alert_level VARCHAR(10) DEFAULT 'HIGH' COMMENT '告警级别：HIGH-高',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-活跃，RESOLVED-已解决',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除'
    ) COMMENT '过载告警记录表';

-- 为过载告警表创建索引
CREATE INDEX idx_overload_line ON overload_alert(line_code);
CREATE INDEX idx_overload_dates ON overload_alert(alert_start_date, alert_end_date);
CREATE INDEX idx_overload_status ON overload_alert(status);
