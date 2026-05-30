-- 为比赛表添加状态字段
ALTER TABLE competition ADD COLUMN status INT DEFAULT 0 COMMENT '比赛状态：0-未开始，1-进行中，2-已结束，3-已取消';

-- 更新现有数据的状态为未开始（默认值）
UPDATE competition SET status = 0 WHERE status IS NULL;
