package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Engine;
import com.autotest.LiuMa.dto.EngineDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EngineMapper {
    void saveEngine(Engine engine);

    void deleteEngine(String id);

    void updateStatus(String id, String status);

    void updateHeartbeat(String id, Long time);

    void updateLostHeartbeatEngine(Long minLastHeartbeatTime);

    Engine getEngineByName(String projectId, String name);

    EngineDTO getEngineById(String id);

    List<Engine> getAllCustomEngine(String projectId);

    List<Engine> getAllSystemEngine();

    List<EngineDTO> getEngineList(String projectId, String condition);
}