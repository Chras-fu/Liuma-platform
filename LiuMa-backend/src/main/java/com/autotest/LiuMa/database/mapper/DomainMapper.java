package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Domain;
import com.autotest.LiuMa.dto.DomainDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DomainMapper {
    void saveDomain(Domain domain);

    void deleteDomain(String id);

    Domain getDomainByName(String environmentId, String domainKey);

    List<Domain> getPathDomainList(String environmentId);

    List<DomainDTO> getDomainList(String environmentId);

}