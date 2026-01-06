-- 1. 创建列车增车建议表
CREATE TABLE IF NOT EXISTS train_addition_suggestion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    line_code VARCHAR(50) NOT NULL COMMENT '线路编码',
    section VARCHAR(100) NOT NULL COMMENT '需要加车的区间',
    suggested_train_number VARCHAR(20) COMMENT '建议车次号',
    departure_time TIME COMMENT '建议发车时间',
    arrival_time TIME COMMENT '建议到达时间',
    carriage_count INT COMMENT '建议编组',
    train_type VARCHAR(50) COMMENT '建议车型',
    reason VARCHAR(500) COMMENT '建议原因',
    expected_load_rate DECIMAL(5, 2) COMMENT '预计满载率',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '建议状态：PENDING-待处理，APPROVED-已批准，REJECTED-已拒绝',
    created_by VARCHAR(20) DEFAULT 'SYSTEM' COMMENT '建议创建者类型：SYSTEM-系统生成，MANUAL-人工添加',
    suggest_date DATE COMMENT '建议生成日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    INDEX idx_line_code (line_code),
    INDEX idx_section (section),
    INDEX idx_status (status),
    INDEX idx_created_by (created_by),
    INDEX idx_suggest_date (suggest_date)
) COMMENT '列车增车建议表';

-- 2. 插入测试数据（系统生成的建议）
INSERT INTO train_addition_suggestion (
    line_code, section, suggested_train_number, departure_time, arrival_time, 
    carriage_count, train_type, reason, expected_load_rate, status, created_by, suggest_date
) VALUES 
('G123', '北京南-上海虹桥', 'G9999', '08:30:00', '12:30:00', 8, 'CR400AF', '该区间在8:00-9:00时段满载率高达95%，建议加开列车', 85.00, 'PENDING', 'SYSTEM', '2024-12-01'),
('G456', '广州南-深圳北', 'G8888', '14:00:00', '15:30:00', 8, 'CR400BF', '该区间在14:00-15:00时段满载率高达92%，建议加开列车', 82.00, 'APPROVED', 'SYSTEM', '2024-12-01'),
('G789', '成都东-重庆北', 'G7777', '09:15:00', '11:45:00', 8, 'CRH380D', '该区间在9:00-10:00时段满载率高达90%，建议加开列车', 80.00, 'REJECTED', 'SYSTEM', '2024-12-02');

-- 3. 插入人工添加的建议
INSERT INTO train_addition_suggestion (
    line_code, section, suggested_train_number, departure_time, arrival_time, 
    carriage_count, train_type, reason, expected_load_rate, status, created_by, suggest_date
) VALUES 
('G101', '上海虹桥-北京南', 'G6666', '10:00:00', '14:30:00', 16, 'CR400AF', '春运期间该线路客流激增，需增加运力', 75.00, 'PENDING', 'MANUAL', '2024-12-03');

-- 4. 查询所有增车建议
SELECT * FROM train_addition_suggestion WHERE is_deleted = 0 ORDER BY create_time DESC;

-- 5. 根据线路查询增车建议
SELECT * FROM train_addition_suggestion 
WHERE is_deleted = 0 AND line_code = 'G123' 
ORDER BY create_time DESC;

-- 6. 根据状态查询增车建议
SELECT * FROM train_addition_suggestion 
WHERE is_deleted = 0 AND status = 'PENDING' 
ORDER BY create_time DESC;

-- 7. 根据创建者类型查询增车建议
SELECT * FROM train_addition_suggestion 
WHERE is_deleted = 0 AND created_by = 'SYSTEM' 
ORDER BY create_time DESC;

-- 8. 更新建议状态
UPDATE train_addition_suggestion 
SET status = 'APPROVED', update_time = NOW() 
WHERE id = 1 AND is_deleted = 0;

-- 9. 逻辑删除建议
UPDATE train_addition_suggestion 
SET is_deleted = 1, update_time = NOW() 
WHERE id = 3;

-- 10. 统计各状态的建议数量
SELECT status, COUNT(*) as count 
FROM train_addition_suggestion 
WHERE is_deleted = 0 
GROUP BY status;