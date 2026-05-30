package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SearchDTO;
import com.sky.entity.Competition;
import com.sky.mapper.CommonMapper;
import com.sky.result.PageResult;
import com.sky.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonServiceImpl implements CommonService {
    @Autowired
    private CommonMapper commonMapper;
    @Override
    public PageResult search(SearchDTO competitionSearchDTO) {
        PageHelper.startPage(competitionSearchDTO.getPage(),competitionSearchDTO.getPageSize());
        Page<Competition> page=commonMapper.page(competitionSearchDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }
}
