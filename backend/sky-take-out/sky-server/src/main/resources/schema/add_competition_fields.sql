-- 为比赛表添加详情字段
ALTER TABLE competition 
ADD COLUMN organizer VARCHAR(255) NULL COMMENT '主办方',
ADD COLUMN location VARCHAR(255) NULL COMMENT '举办地点',
ADD COLUMN participant_limit INT NULL COMMENT '参赛人数限制',
ADD COLUMN prize VARCHAR(500) NULL COMMENT '奖项设置',
ADD COLUMN contact VARCHAR(100) NULL COMMENT '联系方式';
