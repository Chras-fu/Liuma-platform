package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Control;
import com.autotest.LiuMa.dto.ControlDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ControlMapper {
    void addControl(Control control);

    void updateControl(Control control);

    void deleteControl(String id);

    List<Control> getModuleControlList(String projectId, String moduleId, String system);

    List<ControlDTO> getControlList(QueryRequest request);

    ControlDTO getControlById(String id);

    Control getControlByName(String moduleId, String name, String system);
}