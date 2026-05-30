package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.UserPageQueryDTO;
import com.sky.entity.User;
import com.sky.mapper.UserMangerMapper;
import com.sky.result.PageResult;
import com.sky.service.UserMangerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserMangerServiceImpl implements UserMangerService {
    @Autowired
    private UserMangerMapper userMangerMapper;
    @Override
    public void startOrStop(Long id,Integer status) {
        LocalDateTime updateTime = LocalDateTime.now();
        userMangerMapper.update(id,updateTime,status);
    }

    @Override
    public PageResult page(UserPageQueryDTO userPageQueryDTO) {
        PageHelper.startPage(userPageQueryDTO.getPage(),userPageQueryDTO.getPageSize());
        Page<User> page=userMangerMapper.page(userPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void delete(Long id) {
        userMangerMapper.delete(id);
    }
}
