package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.SearchDTO;
import com.sky.entity.Competition;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommonMapper {
    Page<Competition> page(SearchDTO competitionSearchDTO);
}
