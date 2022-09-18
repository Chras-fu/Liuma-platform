package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.CaseApp;
import com.autotest.LiuMa.dto.CaseAppDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CaseAppMapper {
    void addCaseApp(List<CaseApp> caseApps);

    void deleteCaseApp(String caseId);

    List<CaseAppDTO> getCaseAppList(String caseId, String caseType);
}