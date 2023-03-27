package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Case;
import com.autotest.LiuMa.dto.CaseDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CaseMapper {
    void addCase(Case testcase);

    void updateCase(Case testcase);

    void deleteCase(String id);

    String getCaseSystem(String id);

    CaseDTO getCaseDetail(String id);

    List<CaseDTO> getCaseList(QueryRequest request);
}