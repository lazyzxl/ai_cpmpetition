package com.sky.controller.Common;

import com.sky.context.BaseContext;

import com.sky.result.Result;
import com.sky.service.CareService;
import com.sky.service.CompetitionService;
import com.sky.vo.CompetitionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/competition")
@Api(tags = "比赛详情页面")
public class CompetitionController {
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private CareService careService;
    @GetMapping("/{competitionId}")
    @ApiOperation(value="获取比赛详细信息")
    public Result<CompetitionVO> getById(@PathVariable("competitionId") Long competitionId){
        CompetitionVO competitionVO=competitionService.getById(competitionId);
        return Result.success(competitionVO);
    }
    @PostMapping("{competitionId}")
    @ApiOperation(value = "添加消息通知")
    public Result addCompetition(@PathVariable("competitionId") Long competitionId){
        careService.save(competitionId);
        return Result.success();
    }
    @DeleteMapping("{competitionId}")
    @ApiOperation("取消关注")
    public Result deleteCompetition(@PathVariable("competitionId") Long competitionId){
        Long userId= BaseContext.getCurrentId();
        // 先查询该用户关注该比赛的care记录ID，然后删除
        careService.deleteCareByCompetitionId(competitionId, userId);
        return Result.success();
    }
}
