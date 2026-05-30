package com.sky.service.impl;

import com.sky.mapper.commentmapper;
import com.sky.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private commentmapper commentMapper;
}
