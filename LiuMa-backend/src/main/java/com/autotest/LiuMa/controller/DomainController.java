package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.database.domain.Domain;
import com.autotest.LiuMa.dto.DomainDTO;
import com.autotest.LiuMa.service.DomainService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/autotest/domain")
public class DomainController {

    @Resource
    private DomainService domainService;

    @PostMapping("/save")
    public void saveDomain(@RequestBody Domain domain, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        domain.setUpdateUser(user);
        domainService.saveDomain(domain);
    }

    @PostMapping("/delete")
    public void deleteEnvironment(@RequestBody Domain domain) {
        domainService.deleteDomain(domain);
    }

    @GetMapping("/list/{environmentId}")
    public List<DomainDTO> getDomainList(@PathVariable String environmentId) {
        return domainService.getDomainList(environmentId);
    }

}
