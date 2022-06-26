package com.autotest.LiuMa.controller;

import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.dto.ApiDTO;
import com.autotest.LiuMa.dto.CaseDTO;
import com.autotest.LiuMa.request.ApiRequest;
import com.autotest.LiuMa.request.CaseRequest;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.CaseService;
import com.autotest.LiuMa.service.ReportService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/autotest/case")
public class CaseController {

    @Resource
    private CaseService caseService;

    @Resource
    private ReportService reportService;

    @PostMapping("/save")
    public void saveCase(@RequestBody CaseRequest caseRequest, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        caseRequest.setUpdateUser(user);
        caseService.saveCase(caseRequest);
    }

    @PostMapping("/delete")
    public void deleteCase(@RequestBody CaseRequest request) {
        caseService.deleteCase(request);
    }

    @GetMapping("/detail/{caseType}/{caseId}")
    public CaseDTO getCaseDetail(@PathVariable String caseType, @PathVariable String caseId){
        return caseService.getCaseDetail(caseType, caseId);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<CaseDTO>> getCaseList(@PathVariable int goPage, @PathVariable int pageSize,
                                           @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, caseService.getCaseList(request));
    }

    @GetMapping("/api/report/{apiId}")
    public JSONObject getLastApiReport(@PathVariable String apiId){
        return reportService.getLastApiReport(apiId);
    }
}
