package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.exception.DuplicateContentException;
import com.autotest.LiuMa.database.domain.Control;
import com.autotest.LiuMa.database.mapper.ControlMapper;
import com.autotest.LiuMa.dto.ControlDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class ControlService {

    @Resource
    private ControlMapper controlMapper;

    public void saveControl(Control control) {
        Control old = controlMapper.getControlByName(control.getModuleId(), control.getName(), control.getSystem());
        if(control.getId().equals("") || control.getId() == null){ // 新增控件
            if (old != null){
                throw new DuplicateContentException("同页面下控件重名");
            }
            control.setId(UUID.randomUUID().toString());
            control.setCreateTime(System.currentTimeMillis());
            control.setUpdateTime(System.currentTimeMillis());
            control.setCreateUser(control.getUpdateUser());
            control.setStatus("Normal");
            controlMapper.addControl(control);
        }else{ // 修改控件
            if (old != null && !old.getId().equals(control.getId())){
                throw new DuplicateContentException("同页面下控件重名");
            }
            control.setUpdateTime(System.currentTimeMillis());
            controlMapper.updateControl(control);
        }
    }

    public void deleteControl(Control control) {
        controlMapper.deleteControl(control.getId());
    }

    public List<Control> getModuleControlList(String projectId, String moduleId, String system) {
        return controlMapper.getModuleControlList(projectId, moduleId, system);
    }

    public List<ControlDTO> getControlList(QueryRequest request){
        if(request.getCondition() != null && !request.getCondition().equals("")){
            request.setCondition("%"+request.getCondition()+"%");
        }
        return controlMapper.getControlList(request);
    }

}
