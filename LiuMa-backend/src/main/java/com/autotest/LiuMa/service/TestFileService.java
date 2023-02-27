package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.utils.FileUtils;
import com.autotest.LiuMa.database.domain.TestFile;
import com.autotest.LiuMa.database.mapper.TestFileMapper;
import com.autotest.LiuMa.dto.TestFileDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class TestFileService {

    @Value("${test.file.path}")
    public String TEST_FILE_PATH;

    @Value("${app.package.path}")
    public String APP_PACKAGE_PATH;

    @Resource
    private TestFileMapper testFileMapper;

    public void uploadFile(TestFile testFile, MultipartFile file){
        testFile.setId(UUID.randomUUID().toString().replace("-", ""));
        testFile.setCreateTime(System.currentTimeMillis());
        testFile.setUpdateTime(System.currentTimeMillis());
        testFile.setCreateUser(testFile.getUpdateUser());
        // 保存文件
        String path = TEST_FILE_PATH + "/" + testFile.getProjectId() + "/" + testFile.getId();
        String filePath = FileUtils.uploadTestFile(file, path);
        testFile.setFilePath(filePath);
        testFileMapper.saveTestFile(testFile);
    }

    public String uploadPackage(String packageName, MultipartFile file){
        DateFormat dateInstance = DateFormat.getDateInstance();
        String date = dateInstance.format(new Date());
        // 保存文件
        String fileId = UUID.randomUUID().toString();
        String path = APP_PACKAGE_PATH + "/" + date + "/" + fileId;
        FileUtils.uploadTestFile(file, path);
        return "/openapi/download/package/" + date + "/" + fileId + "/" + packageName;
    }

    public void deleteFile(String id) {
        TestFile testFile = testFileMapper.getTestFile(id);
        FileUtils.deleteFile(testFile.getFilePath());
        testFileMapper.deleteTestFile(id);
    }

    public List<TestFile> getAllTestFile(String projectId) {
        return testFileMapper.getAllTestFile(projectId);
    }

    public List<TestFileDTO> getTestFileList(QueryRequest request) {
        if(request.getCondition() != null && !request.getCondition().equals("")){
            request.setCondition("%"+request.getCondition()+"%");
        }
        return testFileMapper.getTestFileList(request.getProjectId(), request.getCondition());
    }

}
