package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.Element;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElementDTO extends Element {
    private String moduleName;

    private String username;
}
