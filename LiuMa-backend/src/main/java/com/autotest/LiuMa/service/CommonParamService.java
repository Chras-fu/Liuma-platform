package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.exception.DuplicateException;
import com.autotest.LiuMa.database.domain.ParamData;
import com.autotest.LiuMa.database.domain.ParamGroup;
import com.autotest.LiuMa.database.mapper.CommonParamMapper;
import com.autotest.LiuMa.dto.ParamDataDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class CommonParamService {

    @Resource
    private CommonParamMapper commonParamMapper;

    public void saveParamData(ParamData paramData) {
        ParamData oldParam = commonParamMapper.getParamByGroupAndName(paramData.getGroupId(), paramData.getName());
        if(oldParam != null && !Objects.equals(oldParam.getId(), paramData.getId())){
            throw new DuplicateException("当前项目已有重名参数");
        }
        if(paramData.getId() == null || paramData.getId().equals("")){
            //新增参数
            paramData.setId(UUID.randomUUID().toString());
            paramData.setCreateTime(System.currentTimeMillis());
            paramData.setUpdateTime(System.currentTimeMillis());
            paramData.setCreateUser(paramData.getUpdateUser());
        }else{
            // 更新参数
            paramData.setUpdateTime(System.currentTimeMillis());
        }
        commonParamMapper.saveParamData(paramData);
    }

    public void deleteParamData(ParamData paramData) {
        commonParamMapper.deleteParamData(paramData.getId());
    }

    public List<ParamDataDTO> getParamDataList(String groupId, String condition){
        if(condition != null && !condition.equals("")){
            condition = ("%"+condition+"%");
        }
        return commonParamMapper.getParamDataList(groupId, condition);
    }

    public List<ParamDataDTO> getParamDataListByGroupName(String groupName, String projectId){
        return commonParamMapper.getParamDataListByGroupName(groupName, projectId);
    }

    public List<ParamData> getCustomParamList(String projectId){
        return commonParamMapper.getCustomParamList(projectId);
    }

    public List<ParamGroup> getParamGroupList(String projectId){
        return commonParamMapper.getParamGroupList(projectId);
    }

}
