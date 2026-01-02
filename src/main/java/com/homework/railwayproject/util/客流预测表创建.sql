-- 创建客流预测表
CREATE TABLE passenger_flow_prediction (
    prediction_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '预测ID',
    site_id INT COMMENT '站点ID',
    site_name VARCHAR(100) COMMENT '站点名称',
    prediction_date DATE COMMENT '预测日期',
    prediction_time TIME COMMENT '预测时段开始时间',
    predicted_boarding_count INT DEFAULT 0 COMMENT '预测的上客量',
    predicted_alighting_count INT DEFAULT 0 COMMENT '预测的下客量',
    predicted_total_flow INT DEFAULT 0 COMMENT '预测的总客流量',
    prediction_method VARCHAR(50) COMMENT '预测方法',
    accuracy DOUBLE COMMENT '预测准确度评估',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_site_prediction (site_id, prediction_date),
    INDEX idx_prediction_date (prediction_date)
) COMMENT='客流预测表';