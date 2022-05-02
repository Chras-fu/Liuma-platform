package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Task;
import com.autotest.LiuMa.dto.TaskDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskMapper {
    void addTask(Task task);

    void updateTask(String status, String id);

    int updateTaskEngine(String engineId, String id);

    void updateEngineAllTask(String status, String engineId);

    List<TaskDTO> getTaskList(String engineId);

    TaskDTO getTaskDetail(String id);

    TaskDTO getToRunTask(String engineId);
}