package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.ParamData;
import com.autotest.LiuMa.database.domain.ParamGroup;
import com.autotest.LiuMa.dto.ParamDataDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommonParamMapper {
    void saveParamData(ParamData paramData);

    void deleteParamData(String id);

    ParamData getParamByGroupAndName(String groupId, String name);

    ParamData getParamById(String id);

    List<ParamDataDTO> getParamDataList(String groupId, String condition);

    List<ParamDataDTO> getParamDataListByGroupName(String groupName, String projectId);

    List<ParamData> getCustomParamList(String projectId);

    List<ParamGroup> getParamGroupList(String projectId);

    void insertParamGroup(List<ParamGroup> paramGroups);
}