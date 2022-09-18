package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.CaseWeb;
import com.autotest.LiuMa.dto.CaseWebDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CaseWebMapper {
    void addCaseWeb(List<CaseWeb> caseWebs);

    void deleteCaseWeb(String caseId);

    List<CaseWebDTO> getCaseWebList(String caseId, String caseType);
}