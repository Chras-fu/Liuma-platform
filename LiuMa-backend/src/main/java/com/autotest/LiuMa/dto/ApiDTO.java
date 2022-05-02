package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.Api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiDTO extends Api {
    private String moduleName;

    private String username;
}
