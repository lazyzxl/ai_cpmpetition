package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Redis 数据初始化器
 * 清理旧的 JDK 序列化格式数据，避免与 JSON 序列化器冲突
 */
@Component
@Slf4j
public class RedisDataInitializer implements CommandLineRunner {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始检查并清理 Redis 中的旧格式数据...");
        
        try {
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                log.info("检测到 {} 个 key，正在检查数据格式...", keys.size());
                
                for (String key : keys) {
                    try {
                        Object value = redisTemplate.opsForValue().get(key);
                        // 如果能正常获取并是预期类型，说明格式正确
                        // 如果抛出异常或返回null，可能是旧格式数据
                    } catch (Exception e) {
                        log.warn("key {} 的数据格式不兼容，已删除: {}", key, e.getMessage());
                        redisTemplate.delete(key);
                    }
                }
            }
            log.info("Redis 数据格式检查完成");
        } catch (Exception e) {
            log.warn("Redis 数据初始化失败（可能 Redis 未启动）: {}", e.getMessage());
        }
    }
}