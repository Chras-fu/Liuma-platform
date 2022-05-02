package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.Engine;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class EngineDTO extends Engine {

    private String username;

    List<TaskDTO> taskList;

}
