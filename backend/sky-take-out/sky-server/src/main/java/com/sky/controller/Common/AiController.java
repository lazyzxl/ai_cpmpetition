package com.sky.controller.Common;
import com.sky.dto.aiDTO;
import com.sky.service.CompetitionService;
import com.sky.service.PostService;
import com.sky.service.impl.AiService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiController {
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private PostService postService;
    @Autowired
    private AiService aiService;
    /**
     * 接口 1: 获取 AI 总结
     * 前端传参: { "resourceType": "competition", "resourceId": 101 }
     */
    @PostMapping("/summary")
    public String getSummary(@RequestBody aiDTO request) {
        String content = "";
        if ("competition".equals(request.getResourceType())) {
            content = competitionService.getById(request.getResourceId()).toString();
        } else if ("post".equals(request.getResourceType())) {
            content = postService.getById(request.getResourceId()).toString();
        } else {
            return "未知的资源类型";
        }

        // 调用 AI 生成总结（优先查缓存，再查数据库，最后调用AI）
        return aiService.generateSummary(content, request.getResourceId(), request.getResourceType());
    }

    /**
     * 接口 2: 向 AI 提问
     * 前端传参: { "resourceType": "post", "resourceId": 101, "question": "这个比赛难吗？" }
     */
    @PostMapping("/chat")
    public String chatWithAi(@RequestBody aiDTO request) {
        String context = "";

        // 【关键点】同样根据类型获取上下文
        if ("competition".equals(request.getResourceType())) {
            // 获取比赛的详细信息作为上下文
            context = competitionService.getById(request.getResourceId()).toString();
        } else if ("post".equals(request.getResourceType())) {
            // 获取帖子的内容及评论区前几条作为上下文
            context = postService.getById(request.getResourceId()).toString();
        }

        // 调用 AI 进行问答（优先查缓存，再查数据库，最后调用AI）
        return aiService.answerQuestion(context, request.getQuestion(), request.getResourceId(), request.getResourceType());
    }
}