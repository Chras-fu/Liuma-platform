package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.DebugData;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface DebugDataMapper {
    void addDebugData(DebugData debugData);

    DebugData getDebugData(String id);

    void deleteDebugData(String id);
}