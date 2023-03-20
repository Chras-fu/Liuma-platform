package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.database.domain.Driver;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.DriverService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/autotest/driver")
public class DriverController {

    @Resource
    private DriverService driverService;

    @PostMapping("/save")
    public void saveDriver(@RequestBody Driver driver) {
        driverService.saveDriver(driver);
    }

    @PostMapping("/delete")
    public void deleteDriver(@RequestBody Driver driver) {
        driverService.deleteDriver(driver.getId());
    }

    @GetMapping("/list/{projectId}")
    public List<Driver> getDriverList(@PathVariable String projectId) {
        return driverService.getDriverList(projectId, null);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<Driver>> getDriverPageList(@PathVariable int goPage, @PathVariable int pageSize,
                                                      @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, driverService.getDriverList(request.getProjectId(), request.getCondition()));
    }
}
