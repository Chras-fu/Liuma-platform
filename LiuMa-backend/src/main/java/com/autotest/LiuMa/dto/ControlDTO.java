package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.Control;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ControlDTO extends Control {
    private String moduleName;

    private String username;
}
