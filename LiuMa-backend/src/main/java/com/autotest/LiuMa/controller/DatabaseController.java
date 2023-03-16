package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.database.domain.Database;
import com.autotest.LiuMa.dto.DatabaseDTO;
import com.autotest.LiuMa.service.DatabaseService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/autotest/database")
public class DatabaseController {

    @Resource
    private DatabaseService databaseService;

    @PostMapping("/save")
    public void saveDatabase(@RequestBody DatabaseDTO database, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        database.setUpdateUser(user);
        databaseService.saveDatabase(database);
    }

    @PostMapping("/delete")
    public void deleteEnvironment(@RequestBody Database database) {
        databaseService.deleteDatabase(database);
    }

    @GetMapping("/name/list/{projectId}")
    public List<String> getDatabaseNameList(@PathVariable String projectId) {
        return databaseService.getDatabaseNameList(projectId);
    }

    @GetMapping("/list/{environmentId}")
    public List<DatabaseDTO> getDatabaseList(@PathVariable String environmentId) {
        return databaseService.getDatabaseList(environmentId);
    }

}
