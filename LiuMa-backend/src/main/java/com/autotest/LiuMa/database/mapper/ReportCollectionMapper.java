package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.ReportCollection;
import com.autotest.LiuMa.dto.ReportCollectionDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface ReportCollectionMapper {
    ReportCollection getReportCollection(String reportId, String collectionId);

    List<ReportCollectionDTO> getReportCollectionList(String reportId);

    void addReportCollection(ReportCollection reportCollection);

    void deleteByReportId(String reportId);
}