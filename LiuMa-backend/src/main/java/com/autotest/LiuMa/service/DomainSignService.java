package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.exception.DuplicateException;
import com.autotest.LiuMa.database.domain.DomainSign;
import com.autotest.LiuMa.database.mapper.DomainSignMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class DomainSignService {

    @Resource
    private DomainSignMapper domainSignMapper;

    public void saveDomainSign(DomainSign domainSign) {
        DomainSign oldDomainSign = domainSignMapper.getDomainSignByName(domainSign.getProjectId(), domainSign.getName());
        if(oldDomainSign != null && !Objects.equals(oldDomainSign.getId(), domainSign.getId())){
            throw new DuplicateException("当前项目已有重名域名标识");
        }
        if(domainSign.getId() == null || domainSign.getId().equals("")){
            //新增域名标识
            domainSign.setId(UUID.randomUUID().toString());
            domainSign.setCreateTime(System.currentTimeMillis());
            domainSign.setUpdateTime(System.currentTimeMillis());
        }else{
            // 更新版本
            domainSign.setUpdateTime(System.currentTimeMillis());
        }
        domainSignMapper.saveDomainSign(domainSign);
    }

    public void deleteDomainSign(String id) {
        domainSignMapper.deleteDomainSign(id);
    }

    public List<DomainSign> getDomainSignList(String projectId, String condition) {
        if(condition != null && !condition.equals("")){
            condition = "%"+condition+"%";
        }
        return domainSignMapper.getDomainSignList(projectId, condition);
    }

}
