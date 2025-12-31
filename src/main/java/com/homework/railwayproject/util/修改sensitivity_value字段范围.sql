-- 修改sensitivity_config表的sensitivity_value字段，解除范围限制
ALTER TABLE sensitivity_config MODIFY COLUMN sensitivity_value DOUBLE(10,2) DEFAULT 0.15 
COMMENT '数值（对于容量配置为整数值，对于灵敏度配置为0.00-1.00之间的值）';