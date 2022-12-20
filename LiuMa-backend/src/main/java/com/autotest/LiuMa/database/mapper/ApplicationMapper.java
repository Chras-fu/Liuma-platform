package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Application;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApplicationMapper {

    Application getApplicationByName(String projectId, String name);

    Application getApplicationById(String id);

    void saveApplication(Application application);

    void deleteApplication(String id);

    List<Application> getApplicationList(String projectId, String condition, String system);
}
