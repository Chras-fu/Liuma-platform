package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.database.domain.User;
import com.autotest.LiuMa.database.domain.UserRole;
import com.autotest.LiuMa.dto.RoleDTO;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.RoleService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/autotest/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<RoleDTO>> getRoleList(@PathVariable int goPage, @PathVariable int pageSize,
                                               @RequestBody QueryRequest queryRequest, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        queryRequest.setRequestUser(user);   // 设置查询人
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, roleService.getRoleList(queryRequest));
    }

    @PostMapping("/user/list/{goPage}/{pageSize}")
    public Pager<List<User>> getRoleUserList(@PathVariable int goPage, @PathVariable int pageSize,
                                             @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, roleService.getRoleUserList(request));
    }

    @PostMapping("/user/delete")
    public void deleteRoleUser(@RequestBody UserRole userRole){
        roleService.deleteRoleUser(userRole);
    }

}
