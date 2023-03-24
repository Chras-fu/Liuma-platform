package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.dto.ModuleDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ModuleMapper {
    void saveModule(ModuleDTO module);

    void deleteModule(String moduleType, String id);

    ModuleDTO getModuleByParentAndName(String moduleType, String name, String parentId, String projectId);

    List<ModuleDTO> getModuleList(String moduleType, String projectId);

    Integer getModuleDataById(String moduleType, String moduleId);
}