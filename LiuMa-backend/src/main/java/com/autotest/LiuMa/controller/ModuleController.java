package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.database.domain.Module;
import com.autotest.LiuMa.dto.ModuleDTO;
import com.autotest.LiuMa.service.ModuleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("/autotest/module")
public class ModuleController {

    @Resource
    private ModuleService moduleService;

    @PostMapping("/save")
    public ModuleDTO save(@RequestBody ModuleDTO moduleDTO, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        moduleDTO.setUpdateUser(user);
        return moduleService.save(moduleDTO);
    }

    @PostMapping("/delete")
    public void delete(@RequestBody ModuleDTO moduleDTO) {
        moduleService.delete(moduleDTO);
    }

    @GetMapping("/list/{moduleType}/{projectId}")
    public List<ModuleDTO> getModuleList(@PathVariable String moduleType, @PathVariable String projectId) {
        return moduleService.getModuleList(moduleType, projectId);
    }

}
