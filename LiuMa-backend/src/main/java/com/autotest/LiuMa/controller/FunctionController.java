package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.database.domain.Function;
import com.autotest.LiuMa.dto.FunctionDTO;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.FunctionService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/autotest/function")
public class FunctionController {

    @Resource
    private FunctionService functionService;

    @PostMapping("/save")
    public void saveFunction(@RequestBody Function function, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        function.setUpdateUser(user);
        functionService.saveFunction(function);
    }

    @PostMapping("/delete")
    public void deleteFunction(@RequestBody Function function) {
        functionService.deleteFunction(function.getId());
    }

    @GetMapping("/detail/{functionId}")
    public Function getFunctionDetail(@PathVariable String functionId) {
        return functionService.getFunctionDetail(functionId);
    }

    @GetMapping("/custom/list/{projectId}")
    public List<Function> getCustomFunctionList(@PathVariable String projectId) {
        return functionService.getCustomFunctionList(projectId);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<FunctionDTO>> getFunctionList(@PathVariable int goPage, @PathVariable int pageSize,
                                                    @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, functionService.getFunctionList(request));
    }
}
