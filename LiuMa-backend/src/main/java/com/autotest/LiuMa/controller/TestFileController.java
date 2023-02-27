package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.database.domain.TestFile;
import com.autotest.LiuMa.dto.TestFileDTO;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.TestFileService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/autotest/file")
public class TestFileController {

    @Resource
    private TestFileService testFileService;

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public void uploadFile(@RequestParam("name") String fileName, @RequestParam("projectId") String projectId, @RequestParam("description") String description,
                    @RequestParam(value = "file", required=false) MultipartFile file, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        TestFile testFile = new TestFile();
        testFile.setName(fileName);
        testFile.setProjectId(projectId);
        testFile.setDescription(description);
        testFile.setUpdateUser(user);
        testFileService.uploadFile(testFile, file);
    }

    @PostMapping(value = "/package/upload", consumes = {"multipart/form-data"})
    public String uploadPackage(@RequestParam("name") String packageName,
                           @RequestParam(value = "file", required=false) MultipartFile file) {
        return testFileService.uploadPackage(packageName, file);
    }

    @PostMapping("/delete")
    public void deleteFile(@RequestBody TestFile testFile) {
        testFileService.deleteFile(testFile.getId());
    }

    @GetMapping("/all/{projectId}")
    public List<TestFile> getAllTestFile(@PathVariable String projectId) {
        return testFileService.getAllTestFile(projectId);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<TestFileDTO>> getTestFileList(@PathVariable int goPage, @PathVariable int pageSize,
                                          @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, testFileService.getTestFileList(request));
    }
}
