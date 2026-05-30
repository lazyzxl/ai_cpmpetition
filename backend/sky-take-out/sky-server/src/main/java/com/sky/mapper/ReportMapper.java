package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.ReportPage;
import com.sky.entity.Report;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReportMapper {
    void insert(Report report);
    
    Page<Report> page(ReportPage reportpage);

    Page<Report> pageByStatus(ReportPage reportpage);
    
    void update(Report report);
    
    Integer getCount();
    
    List<Report> getAll();
    
    @Select("select * from report where id = #{id}")
    Report getById(Long id);
}
