package com.sky.service;

import com.sky.dto.ReportDTO;
import com.sky.dto.ReportPage;
import com.sky.entity.Report;
import com.sky.result.PageResult;
import org.springframework.data.domain.PageRequest;

public interface ReportService {
    void save(ReportDTO reportDTO,Long id);

    PageResult page(ReportPage reportpage);

    void delete(Report report);

    void update(Report report);

    Integer getReportCount();
}
