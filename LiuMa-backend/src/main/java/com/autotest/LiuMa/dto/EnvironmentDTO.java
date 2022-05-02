package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.Environment;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EnvironmentDTO extends Environment {

    private String username;

}
