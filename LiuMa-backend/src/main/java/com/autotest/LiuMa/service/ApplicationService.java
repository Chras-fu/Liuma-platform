package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.exception.DuplicateContentException;
import com.autotest.LiuMa.database.domain.Application;
import com.autotest.LiuMa.database.mapper.ApplicationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class ApplicationService {

    @Resource
    private ApplicationMapper applicationMapper;

    public void saveApplication(Application application) {
        Application oldApplication = applicationMapper.getApplicationByName(application.getProjectId(), application.getName());
        if(oldApplication != null && !Objects.equals(oldApplication.getId(), application.getId())){
            throw new DuplicateContentException("当前项目已有重名应用");
        }
        if(application.getId() == null || application.getId().equals("")){
            //新增版本
            application.setId(UUID.randomUUID().toString());
            application.setCreateTime(System.currentTimeMillis());
            application.setUpdateTime(System.currentTimeMillis());
        }else{
            // 更新版本
            application.setUpdateTime(System.currentTimeMillis());
        }
        applicationMapper.saveApplication(application);
    }

    public void deleteApplication(String id) {
        applicationMapper.deleteApplication(id);
    }

    public List<Application> getApplicationList(String projectId, String condition, String system) {
        if(condition != null && !condition.equals("")){
            condition = "%"+condition+"%";
        }
        return applicationMapper.getApplicationList(projectId, condition, system);
    }

}
