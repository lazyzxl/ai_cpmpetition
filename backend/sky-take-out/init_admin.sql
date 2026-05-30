-- 初始化管理员账号
-- 用户名: admin
-- 密码: admin123 (MD5加密后)
-- 角色: admin

INSERT INTO user (username, password, role, status, create_time, update_time)
VALUES ('admin', '0192023a7bbd73250516f069df18b500', 'admin', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE username = username;