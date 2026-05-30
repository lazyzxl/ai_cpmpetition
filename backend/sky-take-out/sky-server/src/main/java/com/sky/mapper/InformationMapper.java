package com.sky.mapper;

import com.sky.entity.Information;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InformationMapper {
    @Select("select * from information where competition_id=#{competitionId} order by create_time")
    List<Information> getByComptitionId(Long competitionId);
}
