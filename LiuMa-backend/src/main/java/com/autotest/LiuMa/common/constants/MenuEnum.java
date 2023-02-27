package com.autotest.LiuMa.common.constants;

public enum MenuEnum {
    COMMON(100,"公共组件", "icon-gonggongzujian", null, null, null),
    FILE(111,"文件管理", "icon-wenjianguanli", "/common/fileManage", COMMON, PermissionEnum.NORMAL_MENU),
    PARAM(121,"公共参数", "icon-gonggongcanshu", "/common/commonParam", COMMON, PermissionEnum.NORMAL_MENU),
    FUNCTION(131,"函数管理", "icon-hanshuguanli", "/common/funcManage", COMMON, PermissionEnum.NORMAL_MENU),
    OPERATION(141,"操作管理", "icon-caozuoguanli", "/common/operationManage", COMMON, PermissionEnum.NORMAL_MENU),
    ENVIRONMENT(200,"环境中心", "icon-huanjingzhongxin", null, null, null),
    SERVER(211,"环境管理", "icon-huanjingguanli", "/envCenter/envManage", ENVIRONMENT, PermissionEnum.NORMAL_MENU),
    ENGINE(221,"引擎管理", "icon-yinqinguanli", "/envCenter/engineManage", ENVIRONMENT, PermissionEnum.NORMAL_MENU),
    DEVICE(231,"设备管理", "icon-shebeiguanli", "/envCenter/deviceManage", ENVIRONMENT, PermissionEnum.NORMAL_MENU),
    CORE(300,"用例中心", "icon-yonglizhongxin", null, null, null),
    API(311,"接口管理", "icon-jiekouguanli", "/caseCenter/interfaceManage", CORE, PermissionEnum.NORMAL_MENU),
    ELEMENT(321,"元素管理", "icon-yuansuguanli", "/caseCenter/elementManage", CORE, PermissionEnum.NORMAL_MENU),
    CONTROL(326,"控件管理", "icon-kongjianguanli", "/caseCenter/controlManage", CORE, PermissionEnum.NORMAL_MENU),
    CASE(331,"用例管理", "icon-yongliguanli", "/caseCenter/caseManage", CORE, PermissionEnum.NORMAL_MENU),
    TEST(400,"计划管理", "icon-jihuaguanli", null, null, null),
    COLLECTION(411,"测试集合", "icon-ceshijihe", "/planManage/testCollection", TEST, PermissionEnum.NORMAL_MENU),
    PLAN(421,"测试计划", "icon-ceshijihua", "/planManage/testPlan", TEST, PermissionEnum.NORMAL_MENU),
    RESULT(500,"测试追踪", "icon-ceshizhuizong", null, null, null),
    REPORT(511,"测试报告", "icon-ceshibaogao", "/report/testReport", RESULT, PermissionEnum.NORMAL_MENU),
    SYSTEM(600,"系统管理", "icon-xitongguanli", null, null, null),
    USER(611,"用户管理", "icon-yonghuguanli", "/systemManage/user", SYSTEM, PermissionEnum.USER_MENU),
    ROLE(621,"角色管理", "icon-jiaoseguanli", "/systemManage/role", SYSTEM, PermissionEnum.ROLE_MENU),
    PROJECT(631,"项目管理", "icon-xiangmuguanli", "/systemManage/project", SYSTEM, PermissionEnum.PROJECT_MENU);

    public final Integer id;
    public final String name;
    public final String icon;
    public final String path;
    public final MenuEnum father;
    public final PermissionEnum permission;

    MenuEnum(Integer id, String name, String icon, String path, MenuEnum father, PermissionEnum permission) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.path = path;
        this.father = father;
        this.permission = permission;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
