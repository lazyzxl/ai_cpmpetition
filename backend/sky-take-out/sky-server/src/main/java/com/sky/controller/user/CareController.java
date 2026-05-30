package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.CareQueryDTO;
import com.sky.entity.care;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CareService;
import com.sky.vo.CareVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "我的关注比赛")
@Slf4j
@RequestMapping("/user/care")
public class CareController {
    @Autowired
    CareService careService;
    @GetMapping
    @ApiOperation("获取我关注的比赛")
    public Result<PageResult> getCareByUserId(CareQueryDTO careQueryDTO){
        Long userId = BaseContext.getCurrentId();
        System.out.println("aaaaa"+userId);
        log.info("获取{}用户的关注列表",userId);
        careQueryDTO.setUserId(userId);
        PageResult pageResult=careService.getByUserId(careQueryDTO);
        return Result.success(pageResult);
    }
    @GetMapping("/check")
    @ApiOperation(value = "检查是否已关注")
    public Result<Boolean> checkCare(@RequestParam Long competitionId){
        Long userId = BaseContext.getCurrentId();
        boolean exists = careService.checkCare(competitionId, userId);
        return Result.success(exists);
    }
    
    @DeleteMapping("/delete")
    @ApiOperation(value = "批量删除关注")
    public Result deleteCare(@RequestParam List<Long> ids){
        log.info("删除关注记录：{}", ids);
        Long userId = BaseContext.getCurrentId();
        careService.deleteCare(ids, userId);
        return Result.success();
    }
}
