package com.sky.service;

import com.sky.dto.UpdatePassword;
import com.sky.dto.UserForgetPasswordDTO;
import com.sky.dto.UserLoginDTO;
import com.sky.dto.UserPageQueryDTO;
import com.sky.entity.User;
import com.sky.entity.UserRegister;
import com.sky.result.PageResult;

import javax.naming.NameAlreadyBoundException;

public interface UserService {

    User login(UserLoginDTO employeeLoginDTO);
    void save(UserRegister userRegister);

//    void startOrStop(Integer status, Long id);

    public User getById(Long id);

    void forgetPassword(UserForgetPasswordDTO userForgetPasswordDTO);

    void update(User user) ;

    void updatePassword(UpdatePassword updatePassword);
}
