-- 为 report 表添加 update_time 字段
ALTER TABLE report ADD COLUMN update_time DATETIME NULL COMMENT '处理时间';
