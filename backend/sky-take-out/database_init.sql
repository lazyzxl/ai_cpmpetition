-- ================================================
-- 数据库初始化脚本
-- 项目: sky-take-out
-- 生成时间: 2026-05-29
-- ================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS sky_take_out DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE sky_take_out;

-- ================================================
-- 1. 用户表 (user)
-- ================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `profession` VARCHAR(100) DEFAULT NULL COMMENT '专业',
    `status` INT NOT NULL DEFAULT 1 COMMENT '状态 0：锁定 1：正常',
    `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_status` (`status`),
    KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ================================================
-- 2. 比赛表 (competition)
-- ================================================
DROP TABLE IF EXISTS `competition`;
CREATE TABLE `competition` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` VARCHAR(255) NOT NULL COMMENT '比赛名称',
    `title` VARCHAR(255) DEFAULT NULL COMMENT '比赛分类',
    `description` TEXT DEFAULT NULL COMMENT '描述',
    `url` VARCHAR(500) DEFAULT NULL COMMENT '官网地址',
    `date` VARCHAR(100) DEFAULT NULL COMMENT '举办时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `photourl` VARCHAR(500) DEFAULT NULL COMMENT '图片地址',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `organizer` VARCHAR(255) DEFAULT NULL COMMENT '主办方',
    `location` VARCHAR(255) DEFAULT NULL COMMENT '举办地点',
    `participant_limit` INT DEFAULT NULL COMMENT '参赛人数限制',
    `prize` VARCHAR(500) DEFAULT NULL COMMENT '奖项设置',
    `contact` VARCHAR(100) DEFAULT NULL COMMENT '联系方式',
    `status` INT DEFAULT 0 COMMENT '比赛状态：0-未开始，1-进行中，2-已结束，3-已取消',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='比赛表';

-- ================================================
-- 3. 帖子表 (post)
-- ================================================
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户id',
    `competition_id` BIGINT DEFAULT NULL COMMENT '比赛id',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `title` VARCHAR(255) NOT NULL COMMENT '帖子标题',
    `content` TEXT NOT NULL COMMENT '帖子内容',
    `url` VARCHAR(500) DEFAULT NULL COMMENT '跳转链接',
    `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
    `tag` VARCHAR(100) DEFAULT NULL COMMENT '帖子分类标签',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_competition_id` (`competition_id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

-- ================================================
-- 4. 评论表 (comment)
-- ================================================
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `post_id` BIGINT NOT NULL COMMENT '帖子id',
    `user_id` BIGINT NOT NULL COMMENT '用户id',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
    `user_name` VARCHAR(50) DEFAULT NULL COMMENT '用户名（用于前端展示）',
    PRIMARY KEY (`id`),
    KEY `idx_post_id` (`post_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ================================================
-- 5. 消息表 (message)
-- ================================================
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '接收消息的用户id',
    `competition_id` BIGINT DEFAULT NULL COMMENT '比赛id',
    `title` VARCHAR(255) NOT NULL COMMENT '消息标题',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `is_read` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读 0：未读 1：已读',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_competition_id` (`competition_id`),
    KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- ================================================
-- 6. 举报表 (report)
-- ================================================
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `comment_id` BIGINT DEFAULT NULL COMMENT '评论id',
    `comment` TEXT DEFAULT NULL COMMENT '评论内容',
    `create_time` DATETIME NOT NULL COMMENT '举报时间',
    `cause` VARCHAR(500) DEFAULT NULL COMMENT '举报原因',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户id',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人',
    `status` INT NOT NULL DEFAULT 0 COMMENT '状态 0:待处理 1：已通过 2：已驳回',
    `update_time` DATETIME DEFAULT NULL COMMENT '处理时间',
    PRIMARY KEY (`id`),
    KEY `idx_comment_id` (`comment_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报表';

-- ================================================
-- 7. 用户关注表 (user_follow)
-- ================================================
DROP TABLE IF EXISTS `user_follow`;
CREATE TABLE `user_follow` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '关注者用户id',
    `follow_user_id` BIGINT NOT NULL COMMENT '被关注者用户id',
    `create_time` DATETIME NOT NULL COMMENT '关注时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_follow` (`user_id`, `follow_user_id`),
    KEY `idx_follow_user_id` (`follow_user_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

-- ================================================
-- 8. 聊天消息表 (chat_message)
-- ================================================
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `sender_id` BIGINT NOT NULL COMMENT '发送者用户id',
    `receiver_id` BIGINT NOT NULL COMMENT '接收者用户id',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `is_read` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读 0：未读 1：已读',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_sender_id` (`sender_id`),
    KEY `idx_receiver_id` (`receiver_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- ================================================
-- 9. 比赛关注表 (care)
-- ================================================
DROP TABLE IF EXISTS `care`;
CREATE TABLE `care` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `competition_id` BIGINT NOT NULL COMMENT '比赛id',
    `user_id` BIGINT NOT NULL COMMENT '关注的用户id',
    `create_time` DATETIME NOT NULL COMMENT '关注时间',
    `url` VARCHAR(500) DEFAULT NULL COMMENT '跳转链接',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_care` (`competition_id`, `user_id`),
    KEY `idx_competition_id` (`competition_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='比赛关注表';

-- ================================================
-- 10. AI回答表 (ai_answer)
-- ================================================
DROP TABLE IF EXISTS `ai_answer`;
CREATE TABLE `ai_answer` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `content_id` BIGINT DEFAULT NULL COMMENT '关联的id',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户id',
    `question` TEXT DEFAULT NULL COMMENT '问题',
    `type` VARCHAR(50) DEFAULT NULL COMMENT '类型',
    `answer` TEXT DEFAULT NULL COMMENT '回答',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_content_id` (`content_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI回答表';

-- ================================================
-- 11. AI总结表 (ai_summary)
-- ================================================
DROP TABLE IF EXISTS `ai_summary`;
CREATE TABLE `ai_summary` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `resource_id` BIGINT DEFAULT NULL COMMENT '来源',
    `type` VARCHAR(50) DEFAULT NULL COMMENT '类型',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `content` TEXT DEFAULT NULL COMMENT 'ai的生成总结',
    PRIMARY KEY (`id`),
    KEY `idx_resource_id` (`resource_id`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI总结表';

-- ================================================
-- 12. 资讯表 (information)
-- ================================================
DROP TABLE IF EXISTS `information`;
CREATE TABLE `information` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息主键',
    `competition_id` BIGINT DEFAULT NULL COMMENT '相关比赛id',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `create_time` DATETIME NOT NULL COMMENT '发表时间',
    PRIMARY KEY (`id`),
    KEY `idx_competition_id` (`competition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资讯表';

-- ================================================
-- 初始化管理员账号
-- 用户名: admin
-- 密码: admin123 (MD5加密)
-- 角色: admin
-- ================================================
INSERT INTO `user` (`username`, `password`, `role`, `status`, `create_time`, `update_time`)
VALUES ('admin', '0192023a7bbd73250516f069df18b500', 'admin', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE username = username;
