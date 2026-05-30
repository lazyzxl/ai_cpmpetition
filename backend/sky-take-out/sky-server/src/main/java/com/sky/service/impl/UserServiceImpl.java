package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.RoleConstant;
import com.sky.constant.StatusConstant;

import com.sky.context.BaseContext;
import com.sky.dto.UpdatePassword;
import com.sky.dto.UserForgetPasswordDTO;
import com.sky.dto.UserLoginDTO;
import com.sky.dto.UserPageQueryDTO;
import com.sky.entity.User;
import com.sky.entity.UserRegister;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.UserMapper;
import com.sky.result.PageResult;
import com.sky.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.naming.NameAlreadyBoundException;
import java.time.LocalDateTime;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    public User login(UserLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        User user = userMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (user == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //  后期需要进行md5加密，然后再进行比对
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (user.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return user;
    }

    @Override
    public void save(UserRegister userRegister) {
        User user = new User();
        BeanUtils.copyProperties(userRegister,user);
        user.setPassword( DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setRole(RoleConstant.USER);
        user.setUpdateTime(LocalDateTime.now());
        user.setCreateTime(LocalDateTime.now());
        user.setStatus(StatusConstant.ENABLE);
        userMapper.insert(user);
    }



    public PageResult page(UserPageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        Page<User> pageResult= userMapper.page(employeePageQueryDTO);
        return new PageResult(pageResult.getTotal(),pageResult.getResult());
    }

  //  @Override
//    public void startOrStop(Integer status, Long id) {
//        User build = User.builder()
//                .status(status)
//                .id(id)
//                .build();
//        userMapper.update(build);
//     employeeMapper.startOrStop(status,id);
//    }


    @Override
    public User getById(Long id) {
        User user=userMapper.getById(id);
         user.setPassword("*****");
        return user;
    }

    @Override
    public void forgetPassword(UserForgetPasswordDTO userForgetPasswordDTO) {
        User user=userMapper.getByUsernameAndPhone(userForgetPasswordDTO.getUsername(), userForgetPasswordDTO.getPhone());
        if (user==null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }else{
            //修改密码
            user.setPassword(DigestUtils.md5DigestAsHex(userForgetPasswordDTO.getPassword().getBytes()));
            //修改时间
            user.setUpdateTime(LocalDateTime.now());
            userMapper.update(user);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
        }

    @Override
    public void updatePassword(UpdatePassword updatePassword) {
        Long currentId = BaseContext.getCurrentId();
        User user=userMapper.getById(currentId);
        log.info("用户信息{}",user);
        if(DigestUtils.md5DigestAsHex(updatePassword.getOrginalPassword().getBytes()).equals(user.getPassword())) {
            user.setUpdateTime(LocalDateTime.now());
            user.setPassword(DigestUtils.md5DigestAsHex(updatePassword.getNewPassword().getBytes()));
            userMapper.update(user);
        }else {
            throw new PasswordErrorException();
        }
    }
}



