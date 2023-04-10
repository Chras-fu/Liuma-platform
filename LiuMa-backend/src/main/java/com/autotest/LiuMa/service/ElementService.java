package com.autotest.LiuMa.service;

import com.autotest.LiuMa.database.domain.Element;
import com.autotest.LiuMa.database.mapper.ElementMapper;
import com.autotest.LiuMa.dto.ElementDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class ElementService {

    @Resource
    private ElementMapper elementMapper;

    public void saveElement(Element element) {
        if(element.getId().equals("") || element.getId() == null){ // 新增元素
            element.setId(UUID.randomUUID().toString());
            element.setCreateTime(System.currentTimeMillis());
            element.setUpdateTime(System.currentTimeMillis());
            element.setCreateUser(element.getUpdateUser());
            element.setStatus("Normal");
            elementMapper.addElement(element);
        }else{ // 修改元素
            element.setUpdateTime(System.currentTimeMillis());
            elementMapper.updateElement(element);
        }
    }

    public void deleteElement(Element element) {
        elementMapper.deleteElement(element.getId());
    }

    public List<Element> getModuleElementList(String projectId, String moduleId) {
        return elementMapper.getModuleElementList(projectId, moduleId);
    }

    public List<ElementDTO> getElementList(QueryRequest request){
        if(request.getCondition() != null && !request.getCondition().equals("")){
            request.setCondition("%"+request.getCondition()+"%");
        }
        return elementMapper.getElementList(request);
    }

}
