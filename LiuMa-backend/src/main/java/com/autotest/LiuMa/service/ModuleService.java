package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.exception.DuplicateContentException;
import com.autotest.LiuMa.database.mapper.ModuleMapper;
import com.autotest.LiuMa.dto.ModuleDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class ModuleService {

    @Resource
    private ModuleMapper moduleMapper;

    public ModuleDTO save(ModuleDTO module) {
        ModuleDTO oldModule = moduleMapper.getModuleByParentAndName(module.getModuleType(), module.getName(), module.getParentId());
        if(oldModule != null){
            throw new DuplicateContentException("当前父模块下已有重名模块");
        }
        if(module.getId() == null){
            //新增模块
            module.setId(UUID.randomUUID().toString());
            module.setCreateTime(System.currentTimeMillis());
            module.setUpdateTime(System.currentTimeMillis());
            module.setCreateUser(module.getUpdateUser());
        }else{
            // 更新模块
            module.setUpdateTime(System.currentTimeMillis());
        }
        moduleMapper.saveModule(module);
        module.setLabel(module.getName());
        return module;
    }

    public void delete(ModuleDTO module) {
        moduleMapper.deleteModule(module.getModuleType(), module.getId());
        if(module.getChildren() != null) {
            for (ModuleDTO moduleDTO : module.getChildren()) {
                this.delete(moduleDTO);
            }
        }
    }

    public List<ModuleDTO> getModuleList(String moduleType, String projectId){
        List<ModuleDTO> fina = new ArrayList<>();
        List<ModuleDTO> apiModuleDTOS = moduleMapper.getModuleList(moduleType, projectId);
        for(ModuleDTO apiModuleDTO: apiModuleDTOS){
            if(apiModuleDTO.getParentId().equals("0")){
                // 先找到所有的根节点
                apiModuleDTO.setChildren(this.nodeList(apiModuleDTOS, apiModuleDTO.getId()));
                fina.add(apiModuleDTO);
            }
        }
        return fina;
    }

    public List<ModuleDTO> nodeList(List<ModuleDTO> apiModules, String parentId){
        // 递归查找所有的子节点
        List<ModuleDTO> childrenList = new ArrayList<>();
        for(ModuleDTO apiModuleDTO: apiModules){
            if(apiModuleDTO.getParentId().equals(parentId)){
                apiModuleDTO.setChildren(this.nodeList(apiModules, apiModuleDTO.getId()));
                childrenList.add(apiModuleDTO);
            }
        }
        return childrenList;
    }

}
