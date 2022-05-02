package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Api;
import com.autotest.LiuMa.dto.ApiDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApiMapper {
    void addApi(Api api);

    void updateApi(Api api);

    void deleteApi(String id);

    ApiDTO getApiDetail(String id);

    List<ApiDTO> getApiList(QueryRequest request);
}