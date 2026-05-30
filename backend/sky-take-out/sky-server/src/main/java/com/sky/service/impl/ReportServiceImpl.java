package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.MessageDTO;
import com.sky.dto.ReportDTO;
import com.sky.dto.ReportPage;
import com.sky.entity.Comment;
import com.sky.entity.Post;
import com.sky.entity.Report;
import com.sky.mapper.postMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.commentmapper;
import com.sky.result.PageResult;
import com.sky.service.MessageService;
import com.sky.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private commentmapper commentMapper;
    @Autowired
    private postMapper postMapper;
    @Autowired
    private MessageService messageService;


    @Override
    public void save(ReportDTO reportDTO,Long id) {
        Comment comment = commentMapper.getById(reportDTO.getCommentId());
        Report report = new Report();
        Long userId = id;
        if (userId == null) {
            throw new RuntimeException("用户未登录，无法举报");
        }
        report.setCreateUser(userId);
        report.setComment(comment.getContent());
        report.setCommentId(reportDTO.getCommentId());
        report.setCause(reportDTO.getCause());
        report.setUserId(userId);
        report.setCreateTime(LocalDateTime.now());
        report.setStatus(0);
        reportMapper.insert(report);
    }

    @Override
    public PageResult page(ReportPage reportpage) {
        // 设置默认分页参数
        int pageNum = reportpage.getPage() != null ? reportpage.getPage() : 1;
        int pageSize = reportpage.getPageSize() != null ? reportpage.getPageSize() : 10;
        
        PageHelper.startPage(pageNum, pageSize);
        Page<Report> page;
        if (reportpage.getStatus() != null) {
            page = reportMapper.pageByStatus(reportpage);
        } else {
            page = reportMapper.page(reportpage);
        }
        return new PageResult(page.getTotal(), page.getResult());
    }


    @Override
    public void delete(Report report) {
        commentMapper.delete(report.getCommentId());
        reportMapper.update(report);
    }

    @Override
    public void update(Report report) {
        report.setUpdateTime(LocalDateTime.now());
        
        // 如果举报通过(status=1)，需要删除评论并通知帖子作者
        if (report.getStatus() != null && report.getStatus() == 1) {
            // 获取举报信息
            Report existingReport = reportMapper.getById(report.getId());
            if (existingReport != null && existingReport.getCommentId() != null) {
                // 先获取评论信息（在删除之前）
                Comment comment = commentMapper.getById(existingReport.getCommentId());
                
                // 删除评论
                commentMapper.delete(existingReport.getCommentId());
                log.info("举报通过，已删除评论ID: {}", existingReport.getCommentId());
                
                // 获取评论所属的帖子，通知帖子作者
                try {
                    if (comment != null && comment.getPostId() != null) {
                        Post post = postMapper.getById(comment.getPostId());
                        if (post != null && post.getUserId() != null) {
                            // 发送消息通知帖子作者
                            MessageDTO messageDTO = new MessageDTO();
                            messageDTO.setUserId(post.getUserId());
                            messageDTO.setTitle("评论被删除通知");
                            messageDTO.setContent("您帖子「" + post.getTitle() + "」中的一条评论因举报被管理员删除。");
                            messageService.send(messageDTO);
                            log.info("已通知帖子作者ID: {} 评论被删除", post.getUserId());
                        }
                    }
                } catch (Exception e) {
                    log.error("发送消息通知失败：", e);
                }
            }
        }
        
        reportMapper.update(report);
    }

    @Override
    public Integer getReportCount() {
        Integer count=reportMapper.getCount();
        return count;
    }

    private long getUser(){
        return BaseContext.getCurrentId();
    }
}
