package com.sky.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sky.context.BaseContext;
import com.sky.entity.AIAnswer;
import com.sky.entity.AISummary;
import com.sky.mapper.AIAnswerMapper;
import com.sky.mapper.AISummaryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AiService {

    @Autowired
    private AIAnswerMapper aiAnswerMapper;

    @Autowired
    private AISummaryMapper aiSummaryMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 在 application.yml 中配置你的 API Key 和 URL
    @Value("${ai.key}")
    private String apiKey;

    @Value("${ai.url}")
    private String apiUrl;

    /**
     * 通用调用方法
     * @param systemPrompt 系统设定 (角色)
     * @param userContent 用户输入的内容 (上下文 + 问题)
     * @return AI 返回的文本
     */
    public String callAi(String systemPrompt, String userContent) {
        // 构建消息体
        List<JSONObject> messages = new ArrayList<>();

        // 1. 系统指令
        JSONObject sysMsg = new JSONObject();
        sysMsg.put("role", "system");
        sysMsg.put("content", systemPrompt);
        messages.add(sysMsg);

        // 2. 用户内容
        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userContent);
        messages.add(userMsg);

        // 构建请求体 (以通义千问为例)
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "qwen-turbo"); // 模型名称
        requestBody.put("input", new JSONObject().put("messages", messages));

        // 发送 HTTP POST 请求
        String responseStr = HttpRequest.post(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .timeout(30000) // 设置超时时间 30 秒
                .execute()
                .body();

        // 解析响应 (根据具体 API 文档解析)
        JSONObject responseJson = JSONUtil.parseObj(responseStr);
        if (responseJson.containsKey("output")) {
            return responseJson.getJSONObject("output").getStr("text");
        }
        return "AI 服务暂时不可用";
    }

    /**
     * 功能 1: AI 一键总结（带缓存和数据库存储）
     */
    public String generateSummary(String content, Long resourceId, String type) {
        String cacheKey = "ai:summary:" + type + ":" + resourceId;
        
        // 1. 先查Redis缓存
        String cachedSummary = (String) redisTemplate.opsForValue().get(cacheKey);
        if (cachedSummary != null) {
            return cachedSummary;
        }
        
        // 2. 查数据库
        AISummary summary = aiSummaryMapper.getLatestSummary(resourceId, type);
        if (summary != null) {
            // 缓存到Redis，有效期1小时
            redisTemplate.opsForValue().set(cacheKey, summary.getContent(), 1, TimeUnit.HOURS);
            return summary.getContent();
        }
        
        // 3. 调用AI生成 - 优化后的提示词
        String systemPrompt = "你是一个专业的高校竞赛咨询助手，专门帮助大学生了解和分析各类学科竞赛。请从以下竞赛资料中提取关键信息并总结：\n\n" +
                "【提取信息】\n" +
                "1. 竞赛名称（全称/简称）\n" +
                "2. 主办单位\n" +
                "3. 报名时间和比赛时间\n" +
                "4. 参赛对象和要求\n" +
                "5. 参赛形式（个人/团队）\n" +
                "6. 奖励政策\n" +
                "7. 报名方式和费用\n" +
                "8. 官网链接\n\n" +
                "【输出要求】\n" +
                "1. 字数控制在150字以内\n" +
                "2. 使用emoji增加可读性\n" +
                "4. 结尾给出参赛建议或亮点点评\n\n" +
                "请总结以下竞赛信息：";
        String aiSummary = callAi(systemPrompt, content);
        
        // 4. 存储到数据库
        AISummary newSummary = new AISummary();
        newSummary.setResourceId(resourceId);
        newSummary.setType(type);
        newSummary.setContent(aiSummary);
        newSummary.setCreateTime(LocalDateTime.now());
        aiSummaryMapper.insert(newSummary);
        
        // 5. 缓存到Redis
        redisTemplate.opsForValue().set(cacheKey, aiSummary, 1, TimeUnit.HOURS);
        
        return aiSummary;
    }

    /**
     * 功能 2: 基于上下文的问答（带缓存和数据库存储）
     */
    public String answerQuestion(String context, String question, Long contentId, String type) {
        String cacheKey = "ai:answer:" + type + ":" + contentId + ":" + question.hashCode();

        // 1. 先查Redis缓存
        String cachedAnswer = (String) redisTemplate.opsForValue().get(cacheKey);
        if (cachedAnswer != null) {
            return cachedAnswer;
        }

        // 2. 查数据库
        AIAnswer answer = aiAnswerMapper.getLatestAnswer(contentId, type);
        if (answer != null && question.equals(answer.getQuestion())) {
            // 缓存到Redis，有效期30分钟
            redisTemplate.opsForValue().set(cacheKey, answer.getAnswer(), 30, TimeUnit.MINUTES);
            return answer.getAnswer();
        }

        // 3. 调用AI生成 - 优化后的提示词
        String systemPrompt = "你是一个专业、热情的高校竞赛咨询助手，专门帮助大学生了解和参与各类学科竞赛。你的任务是：\n\n" +
                "1. **精准理解**：仔细阅读用户的问题，理解其真正想了解的信息\n" +
                "2. **基于事实**：根据提供的【背景信息】给出准确回答，不要编造信息\n" +
                "3. **结构清晰**：回答时分点说明，使用emoji增加可读性：\n" +
                "   - 🎯 简要回答（一句话概括）\n" +
                "   - 📋 详细说明（分点列出关键信息）\n" +
                "   - 💡 温馨提示（相关建议或注意事项）\n" +
                "4. **情感共鸣**：理解学生参赛的心理，提供鼓励和建议\n" +
                "5. **不懂就说**：如果【背景信息】中没有提到相关内容，直接说'资料中未提及'，不要猜测\n\n" +
                "回答风格：友好、专业、易懂，像学长学姐分享经验一样\n\n" +
                "请根据以下【背景信息】回答用户问题：";
        String userContent = "【背景信息】:\n" + context + "\n\n【用户问题】:\n" + question;
        String aiAnswer = callAi(systemPrompt, userContent);

        // 4. 存储到数据库
        Long userId = BaseContext.getCurrentId();
        AIAnswer newAnswer = new AIAnswer();
        newAnswer.setContentId(contentId);
        newAnswer.setUserId(userId);
        newAnswer.setQuestion(question);
        newAnswer.setType(type);
        newAnswer.setAnswer(aiAnswer);
        newAnswer.setCreateTime(LocalDateTime.now());
        aiAnswerMapper.insert(newAnswer);

        // 5. 缓存到Redis
        redisTemplate.opsForValue().set(cacheKey, aiAnswer, 30, TimeUnit.MINUTES);

        return aiAnswer;
    }
}