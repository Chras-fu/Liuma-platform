package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.database.domain.Control;
import com.autotest.LiuMa.dto.ControlDTO;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.ControlService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/autotest/control")
public class ControlController {

    @Resource
    private ControlService controlService;

    @PostMapping("/save")
    public void saveControl(@RequestBody Control control, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        control.setUpdateUser(user);
        controlService.saveControl(control);
    }

    @PostMapping("/delete")
    public void deleteControl(@RequestBody Control control) {
        controlService.deleteControl(control);
    }

    @PostMapping("/list/module")
    public List<Control> getControlDetail(@RequestBody QueryRequest request){
        return controlService.getModuleControlList(request.getProjectId(), request.getModuleId(), request.getSystem());
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<ControlDTO>> getControlList(@PathVariable int goPage, @PathVariable int pageSize,
                                          @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, controlService.getControlList(request));
    }
}
