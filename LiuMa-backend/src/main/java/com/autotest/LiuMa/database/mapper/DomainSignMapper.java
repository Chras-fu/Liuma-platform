package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.DomainSign;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DomainSignMapper {
    void saveDomainSign(DomainSign domainSign);

    void deleteDomainSign(String id);

    DomainSign getDomainSignByName(String projectId, String name);

    List<DomainSign> getDomainSignList(String projectId, String condition);
}