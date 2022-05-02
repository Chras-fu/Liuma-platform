package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.TestFile;
import com.autotest.LiuMa.dto.TestFileDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TestFileMapper {
    void saveTestFile(TestFile testFile);

    TestFile getTestFile(String id);

    void deleteTestFile(String id);

    List<TestFile> getAllTestFile(String projectId);

    List<TestFileDTO> getTestFileList(String projectId, String condition);
}