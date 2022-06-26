package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.common.constants.OperationType;
import com.autotest.LiuMa.common.exception.DuplicateContentException;
import com.autotest.LiuMa.database.domain.Operation;
import com.autotest.LiuMa.database.mapper.OperationMapper;
import com.autotest.LiuMa.dto.OperationDTO;
import com.autotest.LiuMa.dto.OperationGroupDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class OperationService {

    @Resource
    private OperationMapper operationMapper;

    public void saveOperation(Operation operation){
        if(operation.getElement() != null){
            JSONArray element = JSONArray.parseArray(operation.getElement());
            operation.setElement(element.toJSONString());
        }
        if(operation.getData() != null){
            JSONArray data = JSONArray.parseArray(operation.getData());
            operation.setData(data.toJSONString());
        }
        if(operation.getId().equals("") || operation.getId() == null){ // 新增控件
            Operation oldOperation = operationMapper.getOperationByName(operation.getName(), operation.getProjectId());
            if(oldOperation != null){
                throw new DuplicateContentException("函数名称重复");
            }
            operation.setId(UUID.randomUUID().toString());
            operation.setCreateTime(System.currentTimeMillis());
            operation.setUpdateTime(System.currentTimeMillis());
            operation.setCreateUser(operation.getUpdateUser());
            operationMapper.addOperation(operation);
        }else{ // 修改控件
            operation.setUpdateTime(System.currentTimeMillis());
            operationMapper.updateOperation(operation);
        }
    }

    public void deleteOperation(String id) {
        operationMapper.deleteOperation(id);
    }

    public Operation getOperationDetail(String operationId) {
        return operationMapper.getOperationDetail(operationId);
    }

    public List<OperationGroupDTO> getGroupOperationList(String projectId) {
        List<OperationGroupDTO> operationGroupDTOList = new ArrayList<>();
        List<String> operationTypeList = OperationType.enumList();
        for(String operationType:operationTypeList){
            OperationGroupDTO operationGroup = new OperationGroupDTO();
            operationGroup.setId(operationType);
            operationGroup.setName(OperationType.valueOf(operationType.toUpperCase(Locale.ROOT)).toLabel());
            List<OperationDTO> operationList = operationMapper.getGroupOperationList(projectId, operationType);
            for (OperationDTO operation:operationList){
                JSONArray elements = (JSONArray) JSONArray.parse(operation.getElement());
                for(int i=0; i< elements.size(); i++){
                    JSONObject element = elements.getJSONObject(i);
                    element.put("custom", false);
                    element.put("moduleId", "");
                    element.put("moduleName", "");
                    element.put("id", "");
                    element.put("name", "");
                    element.put("by", "");
                    element.put("expression", "");
                    element.put("selectElements", new JSONArray());
                }
                operation.setElementList(elements);
                operation.setDataList((JSONArray) JSONArray.parse(operation.getData()));
            }
            operationGroup.setOperationList(operationList);
            operationGroupDTOList.add(operationGroup);
        }
        return operationGroupDTOList;
    }

    public List<OperationDTO> getOperationList(QueryRequest request) {
        if(request.getCondition() != null && !request.getCondition().equals("")){
            request.setCondition("%"+request.getCondition()+"%");
        }
        return operationMapper.getOperationList(request.getProjectId(), request.getOperationType(), request.getCondition());
    }

}
