package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.RolePermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PermissionMapper {

    void addRolePermission(List<RolePermission> rolePermissions);

    List<String> getUserPermissionByProject(String projectId, String userId);
}