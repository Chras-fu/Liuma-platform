package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.CollectionCase;
import com.autotest.LiuMa.dto.CollectionCaseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CollectionCaseMapper {
    void addCollectionCase(List<CollectionCase> collectionCases);

    void deleteCollectionCase(String collectionId);

    List<CollectionCaseDTO> getCollectionCaseList(String collectionId);

    Integer getCollectionCaseCount(String collectionId);

    List<String> getCollectionCaseTypes(String collectionId);
}