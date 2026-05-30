package com.sky.controller.admin;

import com.sky.dto.CompetitionDTO;
import com.sky.dto.SearchDTO;
import com.sky.entity.Competition;
import com.sky.dto.MessageDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CommonService;
import com.sky.service.CompetitionService;
import com.sky.service.MessageService;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Api(tags = "管理员竞赛管理")
@RestController
@Slf4j
@RequestMapping("/admin/competition")
public class CompetitionMangerController {
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private AliOssUtil aliOssUtil;
    @Autowired
    private MessageService messageService;
    
    @ApiOperation("管理员分页查询比赛")
    @GetMapping("/selectPage")
    public Result<PageResult> selectPage(SearchDTO searchDTO) {
        log.info("管理员分页查询比赛：{}", searchDTO);
        PageResult page = commonService.search(searchDTO);
        return Result.success(page);
    }
    
    @ApiOperation("管理员搜索比赛")
    @GetMapping("/search")
    public Result<PageResult> search(SearchDTO competitionSearchDTO) {
        log.info("管理员搜索比赛：{}", competitionSearchDTO);
        PageResult page = commonService.search(competitionSearchDTO);
        return Result.success(page);
    }
    
    @ApiOperation("管理员删除比赛")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        log.info("管理员删除比赛：{}", id);
        competitionService.delete(id);
        return Result.success();
    }
    
    @ApiOperation("管理员新增比赛")
    @PostMapping
    public Result save(@RequestBody Competition competition) {
        log.info("管理员新增比赛：{}", competition);
        competition.setCreateTime(LocalDateTime.now());
        competition.setUpdateTime(LocalDateTime.now());
        competitionService.save(competition);
        
        // 发送消息通知关注该比赛的用户
        sendCompetitionNotification(competition.getId(), competition.getName(), "新增");
        
        return Result.success();
    }
    
    @ApiOperation("管理员新增比赛（带照片上传）")
    @PostMapping("/saveWithPhoto")
    public Result saveWithPhoto(CompetitionDTO competitionDTO, @RequestParam(required = false) MultipartFile photo) throws IOException {
        log.info("管理员新增比赛（带照片）：{}", competitionDTO);
        Competition competition = new Competition();
        competition.setName(competitionDTO.getName());
        competition.setTitle(competitionDTO.getTitle());
        competition.setDescription(competitionDTO.getDescription());
        competition.setUrl(competitionDTO.getUrl());
        competition.setDate(competitionDTO.getDate());
        competition.setOrganizer(competitionDTO.getOrganizer());
        competition.setLocation(competitionDTO.getLocation());
        competition.setParticipantLimit(competitionDTO.getParticipantLimit());
        competition.setPrize(competitionDTO.getPrize());
        competition.setContact(competitionDTO.getContact());
        competition.setCreateTime(LocalDateTime.now());
        competition.setUpdateTime(LocalDateTime.now());
        
        // 上传照片
        if (photo != null && !photo.isEmpty()) {
            String originalFilename = photo.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID().toString() + extension;
            String photoUrl = aliOssUtil.upload(photo.getBytes(), objectName);
            competition.setPhotourl(photoUrl);
        }
        
        competitionService.save(competition);
        
        // 发送消息通知关注该比赛的用户
        sendCompetitionNotification(competition.getId(), competition.getName(), "新增");
        
        return Result.success();
    }
    
    @ApiOperation("管理员修改比赛")
    @PutMapping
    public Result update(@RequestBody Competition competition) {
        log.info("管理员修改比赛：{}", competition);
        competition.setUpdateTime(LocalDateTime.now());
        competitionService.update(competition);
        
        // 发送消息通知关注该比赛的用户
        sendCompetitionNotification(competition.getId(), competition.getName(), "修改");
        
        return Result.success();
    }
    
    @ApiOperation("管理员修改比赛（带照片上传）")
    @PutMapping("/updateWithPhoto")
    public Result updateWithPhoto(CompetitionDTO competitionDTO, @RequestParam(required = false) MultipartFile photo) throws IOException {
        log.info("管理员修改比赛（带照片）：{}", competitionDTO);
        Competition competition = new Competition();
        competition.setId(competitionDTO.getId());
        competition.setName(competitionDTO.getName());
        competition.setTitle(competitionDTO.getTitle());
        competition.setDescription(competitionDTO.getDescription());
        competition.setUrl(competitionDTO.getUrl());
        competition.setDate(competitionDTO.getDate());
        competition.setOrganizer(competitionDTO.getOrganizer());
        competition.setLocation(competitionDTO.getLocation());
        competition.setParticipantLimit(competitionDTO.getParticipantLimit());
        competition.setPrize(competitionDTO.getPrize());
        competition.setContact(competitionDTO.getContact());
        competition.setUpdateTime(LocalDateTime.now());
        
        // 上传照片
        if (photo != null && !photo.isEmpty()) {
            String originalFilename = photo.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID().toString() + extension;
            String photoUrl = aliOssUtil.upload(photo.getBytes(), objectName);
            competition.setPhotourl(photoUrl);
        }
        
        competitionService.update(competition);
        
        // 发送消息通知关注该比赛的用户
        sendCompetitionNotification(competition.getId(), competition.getName(), "修改");
        
        return Result.success();
    }
    
    @ApiOperation("管理员获取比赛详情")
    @GetMapping("/getById")
    public Result getById(@RequestParam Long competitionId) {
        log.info("管理员获取比赛详情：{}", competitionId);
        return Result.success(competitionService.getById(competitionId));
    }
    
    /**
     * 发送比赛通知消息给关注该比赛的用户
     */
    private void sendCompetitionNotification(Long competitionId, String competitionName, String action) {
        try {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setCompetitionId(competitionId);
            messageDTO.setTitle("比赛" + action + "通知");
            messageDTO.setContent("您关注的比赛「" + competitionName + "」已被" + action + "，请及时查看最新信息。");
            messageService.sendToCompetitionFollowers(messageDTO);
            log.info("成功向关注比赛{}的用户发送{}通知", competitionId, action);
        } catch (Exception e) {
            log.error("发送比赛通知失败：", e);
        }
    }
}
