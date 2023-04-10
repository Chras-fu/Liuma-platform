package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.exception.DuplicateException;
import com.autotest.LiuMa.database.domain.Version;
import com.autotest.LiuMa.database.mapper.VersionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class VersionService {

    @Resource
    private VersionMapper versionMapper;

    public void saveVersion(Version version) {
        Version oldVersion = versionMapper.getVersionByName(version.getProjectId(), version.getName());
        if(oldVersion != null && !Objects.equals(oldVersion.getId(), version.getId())){
            throw new DuplicateException("当前项目已有重名版本");
        }
        if(version.getId() == null || version.getId().equals("")){
            //新增版本
            version.setId(UUID.randomUUID().toString());
            version.setCreateTime(System.currentTimeMillis());
            version.setUpdateTime(System.currentTimeMillis());
        }else{
            // 更新版本
            version.setUpdateTime(System.currentTimeMillis());
        }
        versionMapper.saveVersion(version);
    }

    public void deleteVersion(String id) {
        versionMapper.deleteVersion(id);
    }

    public List<Version> getVersionList(String projectId, String condition) {
        if(condition != null && !condition.equals("")){
            condition = "%"+condition+"%";
        }
        return versionMapper.getVersionList(projectId, condition);
    }

}
