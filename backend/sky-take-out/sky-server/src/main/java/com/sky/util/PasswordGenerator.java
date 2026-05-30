package com.sky.util;

import org.springframework.util.DigestUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PasswordGenerator implements CommandLineRunner {

    @Override
    public void run(String... args) {
        String password = "admin123";
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println("Password: " + password);
        System.out.println("MD5: " + md5Password);
        System.out.println("SQL: INSERT INTO user (username, password, role, status, create_time, update_time) VALUES ('admin', '" + md5Password + "', 'admin', 1, NOW(), NOW())");
    }
}