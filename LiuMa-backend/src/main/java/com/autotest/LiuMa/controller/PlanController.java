package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.database.domain.PlanNotice;
import com.autotest.LiuMa.dto.PlanDTO;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.PlanService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/autotest/plan")
public class PlanController {

    @Resource
    private PlanService planService;

    @PostMapping("/save")
    public void savePlan(@RequestBody PlanDTO planDTO, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        planDTO.setUpdateUser(user);
        planService.savePlan(planDTO);
    }

    @PostMapping("/save/notice")
    public void savePlanNotice(@RequestBody PlanNotice planNotice) {
        planService.savePlanNotice(planNotice);
    }

    @PostMapping("/delete")
    public void deletePlan(@RequestBody PlanDTO planDTO) {
        planService.deletePlan(planDTO);
    }

    @GetMapping("/notice/{planId}")
    public PlanNotice getPlanNotice(@PathVariable String planId){
        return planService.getPlanNotice(planId);
    }

    @GetMapping("/detail/{planId}")
    public PlanDTO getPlanDetail(@PathVariable String planId){
        return planService.getPlanDetail(planId);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<PlanDTO>> getPlanList(@PathVariable int goPage, @PathVariable int pageSize,
                                           @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, planService.getPlanList(request));
    }
}
