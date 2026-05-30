package com.sky.mapper;

import com.sky.entity.Competition;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CompetitionMapper {

    @Select("select * from competition where id = #{competitionId}")
    Competition getById(Long competitionId);

    @Delete("delete from competition where id=#{competitionId}")
    void delete(Long competitionId);
    
    void insert(Competition competition);
    
    void update(Competition competition);
    
    @Select("select * from competition order by create_time desc limit #{limit}")
    List<Competition> getLatestCompetitions(int limit);
    
    List<Competition> getByIds(List<Long> ids);
    
    @Select("select count(*) from competition")
    Integer count();
    
    @Select("select * from competition order by create_time desc")
    List<Competition> getAll();
}
