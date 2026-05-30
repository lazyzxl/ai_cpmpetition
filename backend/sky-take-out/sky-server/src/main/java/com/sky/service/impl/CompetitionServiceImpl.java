package com.sky.service.impl;

import com.sky.entity.Competition;
import com.sky.mapper.CompetitionMapper;
import com.sky.mapper.InformationMapper;
import com.sky.mapper.postMapper;
import com.sky.service.CompetitionService;
import com.sky.vo.CompetitionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
public class CompetitionServiceImpl implements CompetitionService {
    @Autowired
    private CompetitionMapper competitionMapper;
    @Autowired
    private postMapper postMapper;
    @Autowired
    private InformationMapper informationMapper;
    @Override
    public CompetitionVO getById(Long competitionId) {
        CompetitionVO competitionVO=new CompetitionVO();
        Competition competition=competitionMapper.getById(competitionId);
        BeanUtils.copyProperties(competition,competitionVO);
        competitionVO.setPostList(postMapper.getByCompetition(competitionVO.getId()));
        competitionVO.setInformationList(informationMapper.getByComptitionId(competitionVO.getId()));
        return competitionVO;
    }
    

    @Override
    public void delete(Long competitionId) {
        competitionMapper.delete(competitionId);
    }

    @Override
    public void save(Competition competition) {
        competitionMapper.insert(competition);
    }

    @Override
    public void update(Competition competition) {
        competitionMapper.update(competition);
    }
}
