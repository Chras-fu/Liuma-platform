package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Operation;
import com.autotest.LiuMa.dto.OperationDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OperationMapper {
    void addOperation(Operation operation);

    void updateOperation(Operation operation);

    Operation getOperationDetail(String id, String uiType);

    Operation getOperationByName(String name, String projectId, String uiType, String system);

    List<OperationDTO> getGroupOperationList(String projectId, String uiType, String system, String operationType);

    void deleteOperation(String id, String uiType);

    List<OperationDTO> getOperationList(QueryRequest request);
}