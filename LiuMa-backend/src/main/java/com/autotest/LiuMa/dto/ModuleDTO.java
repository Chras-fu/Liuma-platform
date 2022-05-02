package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.Module;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ModuleDTO extends Module {
    private List<ModuleDTO> children;

    private String label;

    private String moduleType;
}
