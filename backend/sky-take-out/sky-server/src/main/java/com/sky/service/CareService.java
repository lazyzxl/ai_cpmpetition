package com.sky.service;

import com.sky.dto.CareQueryDTO;
import com.sky.entity.care;
import com.sky.result.PageResult;

import java.util.List;

public interface CareService {
    PageResult getByUserId(CareQueryDTO careQueryDTO);

    void deleteCare(List<Long> ids,Long userId);

    void save(Long competitionId);
    
    boolean checkCare(Long competitionId, Long userId);
    
    void deleteCareByCompetitionId(Long competitionId, Long userId);
}
