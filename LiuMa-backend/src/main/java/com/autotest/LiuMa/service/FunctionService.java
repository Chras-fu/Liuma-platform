package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSONArray;
import com.autotest.LiuMa.common.exception.DuplicateContentException;
import com.autotest.LiuMa.database.domain.Function;
import com.autotest.LiuMa.database.mapper.FunctionMapper;
import com.autotest.LiuMa.dto.FunctionDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class FunctionService {

    @Resource
    private FunctionMapper functionMapper;

    public void saveFunction(Function function){
        if(function.getParam() != null){
            JSONArray params = JSONArray.parseArray(function.getParam());
            function.setParam(params.toJSONString());
        }
        if(function.getId().equals("") || function.getId() == null){ // 新增函数
            Function oldFunction = functionMapper.getFunctionByName(function.getName());
            if (oldFunction != null){
                throw new DuplicateContentException("函数名称重复");
            }
            function.setId(UUID.randomUUID().toString());
            function.setCreateTime(System.currentTimeMillis());
            function.setUpdateTime(System.currentTimeMillis());
            function.setCreateUser(function.getUpdateUser());
            functionMapper.addFunction(function);
        }else{ // 修改函数
            function.setUpdateTime(System.currentTimeMillis());
            functionMapper.updateFunction(function);
        }
    }

    public void deleteFunction(String id) {
        functionMapper.deleteFunction(id);
    }

    public Function getFunctionDetail(String functionId) {
        return functionMapper.getFunctionDetail(functionId);
    }
    public List<Function> getCustomFunctionList(String projectId) {
        return functionMapper.getCustomFunctionList(projectId);
    }

    public List<FunctionDTO> getFunctionList(QueryRequest request) {
        if(request.getCondition() != null && !request.getCondition().equals("")){
            request.setCondition("%"+request.getCondition()+"%");
        }
        return functionMapper.getFunctionList(request.getProjectId(), request.getCondition());
    }

}
