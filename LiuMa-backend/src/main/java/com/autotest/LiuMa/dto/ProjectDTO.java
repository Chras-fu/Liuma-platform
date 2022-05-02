package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.Project;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectDTO extends Project {

    private String username;

}
