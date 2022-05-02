package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Collection;
import com.autotest.LiuMa.dto.CollectionDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CollectionMapper {
    void addCollection(Collection collection);

    void updateCollection(Collection collection);

    void deleteCollection(String id);

    CollectionDTO getCollectionDetail(String id);

    List<CollectionDTO> getCollectionList(QueryRequest request);
}