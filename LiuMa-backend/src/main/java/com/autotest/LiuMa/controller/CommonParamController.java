package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.database.domain.ParamData;
import com.autotest.LiuMa.database.domain.ParamGroup;
import com.autotest.LiuMa.dto.ParamDataDTO;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.CommonParamService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/autotest/commonParam")
public class CommonParamController {

    @Resource
    private CommonParamService commonParamService;

    @PostMapping("/param/save")
    public void saveParamData(@RequestBody ParamData paramData, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        paramData.setUpdateUser(user);
        commonParamService.saveParamData(paramData);
    }

    @PostMapping("/param/delete")
    public void deleteParamData(@RequestBody ParamData paramData) {
        commonParamService.deleteParamData(paramData);
    }

    @PostMapping("/param/{groupId}/list/{goPage}/{pageSize}")
    public Pager<List<ParamDataDTO>> getParamDataList(@PathVariable int goPage, @PathVariable int pageSize,
                                                      @PathVariable String groupId, @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, commonParamService.getParamDataList(groupId, request.getCondition()));
    }

    @GetMapping("/param/list/{groupName}/{projectId}")
    public List<ParamDataDTO> getParamDataList(@PathVariable String groupName, @PathVariable String projectId) {
        return commonParamService.getParamDataListByGroupName(groupName, projectId);
    }

    @GetMapping("/custom/list/{projectId}")
    public List<ParamData> getCustomParamList(@PathVariable String projectId) {
        return commonParamService.getCustomParamList(projectId);
    }

    @GetMapping("/group/list/{projectId}")
    public List<ParamGroup> getParamGroupList(@PathVariable String projectId) {
        return commonParamService.getParamGroupList(projectId);
    }
}
