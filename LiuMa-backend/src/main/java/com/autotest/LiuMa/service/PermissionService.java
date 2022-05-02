package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.constants.MenuEnum;
import com.autotest.LiuMa.common.constants.PermissionEnum;
import com.autotest.LiuMa.database.mapper.PermissionMapper;
import com.autotest.LiuMa.database.mapper.VersionMapper;
import com.autotest.LiuMa.dto.MenuDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class PermissionService {

    @Resource
    private VersionMapper versionMapper;

    @Resource
    private PermissionMapper permissionMapper;

    public List<MenuDTO> getMenus(String userId, String projectId) {
        List<String> permissions = permissionMapper.getUserPermissionByProject(projectId, userId);
        List<MenuDTO> menuDTOS = new ArrayList<>();
        MenuEnum[] menuList = MenuEnum.values();
        for (MenuEnum menu:menuList){
            if(menu.father == null){
                List<MenuDTO> menus = new ArrayList<>();
                for (MenuEnum chMenu:menuList){
                    if (chMenu.father == menu){
                        if (!permissions.contains(chMenu.permission.id)){
                            continue;   // 没有该权限跳过
                        }
                        MenuDTO chMenuDTO = new MenuDTO();
                        chMenuDTO.setId(chMenu.id);
                        chMenuDTO.setName(chMenu.name);
                        chMenuDTO.setIcon(chMenu.icon);
                        chMenuDTO.setPath(chMenu.path);
                        menus.add(chMenuDTO);
                    }
                }
                if(menus.size() == 0){
                    continue;
                }
                MenuDTO menuDTO = new MenuDTO();
                menuDTO.setId(menu.id);
                menuDTO.setName(menu.name);
                menuDTO.setIcon(menu.icon);
                menuDTO.setPath(menu.path);
                menuDTO.setMenus(menus);
                menuDTOS.add(menuDTO);
            }
        }
        return menuDTOS;
    }

    public Boolean getSettingOptionPermission(String userId, String projectId) {
        List<String> permissions = permissionMapper.getUserPermissionByProject(projectId, userId);
        return permissions.contains(PermissionEnum.SETTING_OPT.id);
    }

}
