package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Element;
import com.autotest.LiuMa.dto.ElementDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ElementMapper {
    void addElement(Element element);

    void updateElement(Element element);

    void deleteElement(String id);

    List<Element> getModuleElementList(String projectId, String moduleId);

    List<ElementDTO> getElementList(QueryRequest request);

    ElementDTO getElementById(String id);
}