-- ================================================
-- 用户关注表和聊天消息表
-- ================================================

-- 用户关注表
DROP TABLE IF EXISTS `user_follow`;
CREATE TABLE `user_follow` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '关注者用户ID',
    `follow_user_id` BIGINT NOT NULL COMMENT '被关注者用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    UNIQUE KEY `uk_user_follow` (`user_id`, `follow_user_id`),
    KEY `idx_follow_user_id` (`follow_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

-- 聊天消息表
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '发送者用户ID',
    `to_user_id` BIGINT NOT NULL COMMENT '接收者用户ID',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `is_read` TINYINT(1) DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_to_user_id` (`to_user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- ================================================
-- 测试数据（可选）
-- ================================================

-- 插入测试关注关系
INSERT INTO `user_follow` (`user_id`, `follow_user_id`) VALUES
(1, 2),
(1, 3),
(2, 1),
(3, 1);

-- 插入测试聊天消息
INSERT INTO `chat_message` (`user_id`, `to_user_id`, `content`, `is_read`) VALUES
(1, 2, '你好，请问比赛报名截止了吗？', 1),
(2, 1, '还没有，5月30号截止。', 1),
(1, 2, '好的，谢谢！', 0),
(3, 1, '这个比赛我也想参加', 0);
