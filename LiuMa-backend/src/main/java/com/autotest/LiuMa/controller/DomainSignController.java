package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.database.domain.DomainSign;
import com.autotest.LiuMa.database.domain.Version;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.DomainSignService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/autotest/domainSign")
public class DomainSignController {

    @Resource
    private DomainSignService domainSignService;

    @PostMapping("/save")
    public void saveDomainSign(@RequestBody DomainSign domainSign) {
        domainSignService.saveDomainSign(domainSign);
    }

    @PostMapping("/delete")
    public void deleteDomainSign(@RequestBody DomainSign domainSign) {
        domainSignService.deleteDomainSign(domainSign.getId());
    }

    @GetMapping("/list/{projectId}")
    public List<DomainSign> getDomainSignList(@PathVariable String projectId) {
        return domainSignService.getDomainSignList(projectId, null);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<DomainSign>> getDomainSignPageList(@PathVariable int goPage, @PathVariable int pageSize,
                                                   @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, domainSignService.getDomainSignList(request.getProjectId(), request.getCondition()));
    }
}
