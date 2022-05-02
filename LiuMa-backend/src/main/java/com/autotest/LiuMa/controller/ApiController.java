package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.dto.ApiDTO;
import com.autotest.LiuMa.request.ApiRequest;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.ApiService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/autotest/api")
public class ApiController {

    @Resource
    private ApiService apiService;

    @PostMapping("/save")
    public void saveApi(@RequestBody ApiRequest apiRequest, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        apiRequest.setUpdateUser(user);
        apiService.saveApi(apiRequest);
    }

    @PostMapping("/delete")
    public void deleteApi(@RequestBody ApiRequest request) {
        apiService.deleteApi(request);
    }

    @GetMapping("/detail/{apiId}")
    public ApiDTO getApiDetail(@PathVariable String apiId){
        return apiService.getApiDetail(apiId);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<ApiDTO>> getApiList(@PathVariable int goPage, @PathVariable int pageSize,
                                          @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, apiService.getApiList(request));
    }
}
