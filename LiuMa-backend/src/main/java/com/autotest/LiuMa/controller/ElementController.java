package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.database.domain.Element;
import com.autotest.LiuMa.dto.ElementDTO;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.ElementService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/autotest/element")
public class ElementController {

    @Resource
    private ElementService elementService;

    @PostMapping("/save")
    public void saveElement(@RequestBody Element element, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        element.setUpdateUser(user);
        elementService.saveElement(element);
    }

    @PostMapping("/delete")
    public void deleteElement(@RequestBody Element element) {
        elementService.deleteElement(element);
    }

    @PostMapping("/list/module")
    public List<Element> getElementDetail(@RequestBody QueryRequest request){
        return elementService.getModuleElementList(request.getProjectId(), request.getModuleId());
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<ElementDTO>> getElementList(@PathVariable int goPage, @PathVariable int pageSize,
                                          @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, elementService.getElementList(request));
    }
}
