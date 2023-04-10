package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.exception.DuplicateException;
import com.autotest.LiuMa.database.domain.Environment;
import com.autotest.LiuMa.database.mapper.EnvironmentMapper;
import com.autotest.LiuMa.dto.EnvironmentDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class EnvironmentService {

    @Resource
    private EnvironmentMapper environmentMapper;

    public void saveEnvironment(Environment environment) {
        Environment oldEnvironment = environmentMapper.getEnvironmentByName(environment.getProjectId(), environment.getName());
        if(oldEnvironment != null && !Objects.equals(oldEnvironment.getId(), environment.getId())){
            throw new DuplicateException("当前项目已有重名环境");
        }
        if(environment.getId() == null || environment.getId().equals("")){
            //新增环境
            environment.setId(UUID.randomUUID().toString());
            environment.setCreateTime(System.currentTimeMillis());
            environment.setUpdateTime(System.currentTimeMillis());
            environment.setCreateUser(environment.getUpdateUser());
        }else{
            // 更新环境
            environment.setUpdateTime(System.currentTimeMillis());
        }
        environmentMapper.saveEnvironment(environment);
    }

    public void deleteEnvironment(Environment environment) {
        environmentMapper.deleteEnvironment(environment.getId());
    }

    public List<Environment> getAllEnvironment(String projectId){
        return environmentMapper.getAllEnvironment(projectId);
    }

    public List<EnvironmentDTO> getEnvironmentList(String projectId, String condition){
        if(condition != null && !condition.equals("")){
            condition = ("%"+condition+"%");
        }
        return environmentMapper.getEnvironmentList(projectId, condition);
    }


}
