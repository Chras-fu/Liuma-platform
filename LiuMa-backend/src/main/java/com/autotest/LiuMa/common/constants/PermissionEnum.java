package com.autotest.LiuMa.common.constants;

public enum PermissionEnum {
    NORMAL_MENU("NORMAL_MENU", "平台常规菜单"),
    USER_MENU("USER_MENU", "用户管理菜单"),
    ROLE_MENU("ROLE_MENU", "角色管理菜单"),
    PROJECT_MENU("PROJECT_MENU", "项目管理菜单"),
    SETTING_MENU("SETTING_MENU", "配置中心菜单"),
    SETTING_OPT("SETTING_OPT", "配置中心操作");

    public final String id;
    public final String name;

    PermissionEnum(String id, String name) {
        this.name = name;
        this.id = id;
    }
}
