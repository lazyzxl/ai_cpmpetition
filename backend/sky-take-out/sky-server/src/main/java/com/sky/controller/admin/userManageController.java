package com.sky.controller.admin;

import com.sky.dto.UserPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.UserMangerService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/user")
public class userManageController {
    @Autowired
    private UserMangerService userMangerService;
    @ApiOperation(value = "启用用户")
    @PostMapping("/enable/{id}")
    public Result enableUser(@PathVariable Long id){
        userMangerService.startOrStop(id, 1);
        return Result.success();
    }
    @ApiOperation(value = "禁用用户")
    @PostMapping("/disable/{id}")
    public Result disableUser(@PathVariable Long id){
        userMangerService.startOrStop(id, 0);
        return Result.success();
    }
    @ApiOperation(value = "删除用户")
    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable Long id){
        userMangerService.delete(id);
        return Result.success();
    }
    @GetMapping("/selectPage")
    @ApiOperation(value = "分页查询用户")
    public Result<PageResult> Page(UserPageQueryDTO userPageQueryDTO){
        log.info("搜索用户{}",userPageQueryDTO);
        PageResult pageResult=userMangerService.page(userPageQueryDTO);
        return Result.success(pageResult);
    }
}
