package com.sky.service;

import com.sky.dto.UserPageQueryDTO;
import com.sky.result.PageResult;

public interface UserMangerService {
    void startOrStop(Long id,Integer status);

    PageResult page(UserPageQueryDTO userPageQueryDTO);

    void delete(Long id);
}
