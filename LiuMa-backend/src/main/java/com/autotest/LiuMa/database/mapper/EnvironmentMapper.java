package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Environment;
import com.autotest.LiuMa.database.domain.ParamData;
import com.autotest.LiuMa.dto.EnvironmentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EnvironmentMapper {
    void saveEnvironment(Environment environment);

    void deleteEnvironment(String id);

    Environment getEnvironmentByName(String projectId, String name);

    List<Environment> getAllEnvironment(String projectId);

    List<EnvironmentDTO> getEnvironmentList(String projectId, String condition);
}