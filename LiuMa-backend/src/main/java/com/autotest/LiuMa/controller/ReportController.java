package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.database.domain.Report;
import com.autotest.LiuMa.dto.ReportCollectionCaseDTO;
import com.autotest.LiuMa.dto.ReportDTO;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.ReportService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/autotest/report")
public class ReportController {

    @Resource
    private ReportService reportService;

    @GetMapping("/debug/{taskId}")
    public ReportCollectionCaseDTO getCaseResult(@PathVariable String taskId){
        return reportService.getCaseResult(taskId);
    }

    @GetMapping("/run/{reportId}")
    public ReportDTO getPlanResult(@PathVariable String reportId){
        return reportService.getPlanResult(reportId);
    }

    @PostMapping("/delete")
    public void deleteReport(@RequestBody Report report) {
        reportService.deleteReport(report);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<ReportDTO>> getReportList(@PathVariable int goPage, @PathVariable int pageSize,
                                                     @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, reportService.getReportList(request));
    }

}
