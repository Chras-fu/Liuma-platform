package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.CaseApi;
import com.autotest.LiuMa.dto.CaseApiDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CaseApiMapper {
    void addCaseApi(List<CaseApi> caseApis);

    void deleteCaseApi(String caseId);

    List<CaseApiDTO> getCaseApiList(String caseId);
}