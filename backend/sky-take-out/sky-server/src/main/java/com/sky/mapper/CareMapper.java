package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CareQueryDTO;
import com.sky.entity.care;
import com.sky.vo.CareItemVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CareMapper {

    Page<care> page(CareQueryDTO careQueryDTO);
    
    Page<CareItemVO> pageWithCompetition(CareQueryDTO careQueryDTO);

    void delete(List<Long> ids);
    @Insert("insert into care (competition_id,user_id,create_time,url) values (#{competitionId},#{userId},#{createTime},#{url})")
    void insert(care care);
    
    @Select("select user_id from care where competition_id = #{competitionId}")
    List<Long> getUserIdsByCompetitionId(Long competitionId);
    
    @Select("select count(*) from care where competition_id = #{competitionId}")
    int countByCompetitionId(Long competitionId);
    
    @Select("select count(*) from care where competition_id = #{competitionId} and user_id = #{userId}")
    int countByCompetitionAndUserId(Long competitionId, Long userId);
    
    void deleteByCompetitionIdAndUserId(Long competitionId, Long userId);
}
