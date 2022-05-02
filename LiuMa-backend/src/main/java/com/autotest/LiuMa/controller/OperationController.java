package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.database.domain.Operation;
import com.autotest.LiuMa.dto.OperationDTO;
import com.autotest.LiuMa.dto.OperationGroupDTO;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.OperationService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/autotest/operation")
public class OperationController {

    @Resource
    private OperationService operationService;

    @PostMapping("/save")
    public void saveOperation(@RequestBody Operation operation, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        operation.setUpdateUser(user);
        operationService.saveOperation(operation);
    }

    @PostMapping("/delete")
    public void deleteOperation(@RequestBody Operation operation) {
        operationService.deleteOperation(operation.getId());
    }

    @GetMapping("/detail/{operationId}")
    public Operation getOperationDetail(@PathVariable String operationId) {
        return operationService.getOperationDetail(operationId);
    }

    @GetMapping("/group/list/{projectId}")
    public List<OperationGroupDTO> getGroupOperationList(@PathVariable String projectId) {
        return operationService.getGroupOperationList(projectId);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<OperationDTO>> getOperationList(@PathVariable int goPage, @PathVariable int pageSize,
                                                    @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, operationService.getOperationList(request));
    }
}
