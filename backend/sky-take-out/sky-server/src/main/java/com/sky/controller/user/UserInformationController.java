package com.sky.controller.user;

import com.sky.dto.UpdatePassword;
import com.sky.entity.User;
import com.sky.result.Result;
import com.sky.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户我的信息")
@RestController
@Slf4j
@RequestMapping("/user/information")
public class UserInformationController {
    @Autowired
    private UserService userService;
    @ApiOperation(value = "信息回显")
    @GetMapping
    public Result<User> getById(Long id){
        User user=userService.getById(id);
        return Result.success(user);
    }
    @PutMapping
    @ApiOperation(value = "更改信息")
    public Result update(@RequestBody User user)  {
        userService.update(user);
        return Result.success();
    }
    @PutMapping("/updatePassword")
    @ApiOperation("用户修改密码")
    public Result updatePassword(@RequestBody UpdatePassword updatePassword){
        log.info("修改密码{}",updatePassword);
        userService.updatePassword(updatePassword);
        return Result.success();
    }
}
