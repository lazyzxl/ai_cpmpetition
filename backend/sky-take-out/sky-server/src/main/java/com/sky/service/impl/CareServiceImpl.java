package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.CareQueryDTO;
import com.sky.entity.care;
import com.sky.mapper.CareMapper;
import com.sky.result.PageResult;
import com.sky.service.CareService;
import com.sky.vo.CareItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CareServiceImpl implements CareService {
    @Autowired
    CareMapper careMapper;
    @Override
    public PageResult getByUserId(CareQueryDTO careQueryDTO) {
        PageHelper.startPage(careQueryDTO.getPage(),careQueryDTO.getPageSize());
        Page<CareItemVO> pageResult= careMapper.pageWithCompetition(careQueryDTO);
        return new PageResult(pageResult.getTotal(),pageResult.getResult());
    }

    @Override
    public void deleteCare(List<Long> ids,Long userId) {
        careMapper.delete(ids);
    }

    @Override
    public void save(Long competitionId) {
        Long userId = BaseContext.getCurrentId();
        
        // 检查是否已关注
        if (isCareExists(competitionId, userId)) {
            throw new RuntimeException("已关注该比赛");
        }
        
        care care=new care();
        care.setCompetitionId(competitionId);
        care.setUrl("competition/"+competitionId);
        care.setCreateTime(LocalDateTime.now());
        care.setUserId(userId);
        careMapper.insert(care);
    }
    
    private boolean isCareExists(Long competitionId, Long userId) {
        return careMapper.countByCompetitionAndUserId(competitionId, userId) > 0;
    }
    
    @Override
    public boolean checkCare(Long competitionId, Long userId) {
        return isCareExists(competitionId, userId);
    }
    
    @Override
    public void deleteCareByCompetitionId(Long competitionId, Long userId) {
        careMapper.deleteByCompetitionIdAndUserId(competitionId, userId);
    }
}
