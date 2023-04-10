package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.exception.DuplicateException;
import com.autotest.LiuMa.database.domain.Domain;
import com.autotest.LiuMa.database.mapper.DomainMapper;
import com.autotest.LiuMa.dto.DomainDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class DomainService {

    @Resource
    private DomainMapper domainMapper;

    public void saveDomain(Domain domain) {
        Domain oldDomain = domainMapper.getDomainByName(domain.getEnvironmentId(), domain.getDomainKey());
        if(oldDomain != null && !Objects.equals(oldDomain.getId(), domain.getId())){
            throw new DuplicateException("当前环境已有重名匹配标识");
        }
        if(domain.getId() == null || domain.getId().equals("")){
            //新增域名
            domain.setId(UUID.randomUUID().toString());
            domain.setCreateTime(System.currentTimeMillis());
            domain.setUpdateTime(System.currentTimeMillis());
            domain.setCreateUser(domain.getUpdateUser());
        }else{
            // 更新域名
            domain.setUpdateTime(System.currentTimeMillis());
        }
        domainMapper.saveDomain(domain);
    }

    public void deleteDomain(Domain domain) {
        domainMapper.deleteDomain(domain.getId());
    }

    public List<DomainDTO> getDomainList(String environmentId){
        return domainMapper.getDomainList(environmentId);
    }

}
