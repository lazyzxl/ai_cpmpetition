-- 确保admin用户存在且role为admin
-- 用户名: admin
-- 密码: admin123 (MD5加密)
-- 角色: admin

INSERT INTO user (username, password, role, status, create_time, update_time)
VALUES ('admin', '0192023a7bbd73250516f069df18b500', 'admin', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    password = VALUES(password),
    role = VALUES(role),
    status = VALUES(status),
    update_time = NOW();

-- 确保其他普通用户的role默认为user
UPDATE user SET role = 'user' WHERE role IS NULL OR role = '';