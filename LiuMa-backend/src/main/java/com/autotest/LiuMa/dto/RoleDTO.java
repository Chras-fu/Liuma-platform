package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDTO extends Role {

    private String projectName;

}
