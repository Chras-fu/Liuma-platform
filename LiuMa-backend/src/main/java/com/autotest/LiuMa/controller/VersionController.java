package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.database.domain.Version;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.VersionService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/autotest/version")
public class VersionController {

    @Resource
    private VersionService versionService;

    @PostMapping("/save")
    public void saveVersion(@RequestBody Version version) {
        versionService.saveVersion(version);
    }

    @PostMapping("/delete")
    public void deleteVersion(@RequestBody Version version) {
        versionService.deleteVersion(version.getId());
    }

    @GetMapping("/list/{projectId}")
    public List<Version> getVersionList(@PathVariable String projectId) {
        return versionService.getVersionList(projectId, null);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<Version>> getVersionPageList(@PathVariable int goPage, @PathVariable int pageSize,
                                                      @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, versionService.getVersionList(request.getProjectId(), request.getCondition()));
    }
}
