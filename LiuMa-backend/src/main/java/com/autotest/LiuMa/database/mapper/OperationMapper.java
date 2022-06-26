package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Operation;
import com.autotest.LiuMa.dto.OperationDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OperationMapper {
    void addOperation(Operation operation);

    void updateOperation(Operation operation);

    Operation getOperationDetail(String id);

    Operation getOperationByName(String name, String projectId);

    List<OperationDTO> getGroupOperationList(String projectId, String operationType);

    void deleteOperation(String id);

    List<OperationDTO> getOperationList(String projectId, String operationType, String condition);
}