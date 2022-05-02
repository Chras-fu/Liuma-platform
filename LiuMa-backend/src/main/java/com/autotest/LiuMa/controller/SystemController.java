package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.database.domain.Assertion;
import com.autotest.LiuMa.service.SystemService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/autotest/system")
public class SystemController {

    @Resource
    private SystemService systemService;

    @GetMapping("/assertion/list")
    public List<Assertion> getAssertion() {
        return systemService.getAssertion();
    }

}
