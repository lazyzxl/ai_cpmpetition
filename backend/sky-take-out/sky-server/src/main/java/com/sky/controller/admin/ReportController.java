package com.sky.controller.admin;

import com.sky.dto.ReportPage;
import com.sky.entity.Report;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.ReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@Api(tags = "举报管理")
@RestController
@RequestMapping("/admin/report")
public class ReportController {
    @Autowired
    private ReportService reportService;
    
    @GetMapping
    public Result<Integer> getReportCount(){
        Integer count=reportService.getReportCount();
        return Result.success(count);
    }
    
    @ApiOperation("举报分页查询")
    @GetMapping("/selectPage")
    public Result<PageResult> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        ReportPage reportPage = new ReportPage();
        reportPage.setPage(page);
        reportPage.setPageSize(pageSize);
        reportPage.setStatus(status);
        reportPage.setStartTime(startTime);
        reportPage.setEndTime(endTime);
        PageResult pageResult = reportService.page(reportPage);
        return Result.success(pageResult);
    }
    
    @ApiOperation("处理举报")
    @PutMapping("/{id}")
    public Result handleReport(@PathVariable Long id, @RequestBody Report report) {
        report.setId(id);
        reportService.update(report);
        return Result.success();
    }
    
    @ApiOperation("删除举报")
    @DeleteMapping("/{id}")
    public Result deleteReport(@PathVariable Long id) {
        Report report = new Report();
        report.setId(id);
        reportService.delete(report);
        return Result.success();
    }
}
