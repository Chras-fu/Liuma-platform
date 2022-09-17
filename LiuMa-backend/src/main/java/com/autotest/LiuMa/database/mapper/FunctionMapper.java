package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Function;
import com.autotest.LiuMa.dto.FunctionDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FunctionMapper {
    void addFunction(Function function);

    void updateFunction(Function function);

    Function getFunctionByName(String projectId, String name);

    Function getFunctionDetail(String id);

    List<Function> getCustomFunctionList(String projectId);

    void deleteFunction(String id);

    List<FunctionDTO> getFunctionList(String projectId, String condition);
}