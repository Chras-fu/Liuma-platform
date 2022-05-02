package com.autotest.LiuMa.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MenuDTO {

    private Integer id;

    private String name;

    private String icon;

    private String path;

    private List<MenuDTO> menus;

}
