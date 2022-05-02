package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Role;
import com.autotest.LiuMa.database.domain.RolePermission;
import com.autotest.LiuMa.database.domain.User;
import com.autotest.LiuMa.database.domain.UserRole;
import com.autotest.LiuMa.dto.RoleDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface RoleMapper {

    Role getRoleByProjectAndName(String projectId, String name);

    UserRole getRoleUser(String roleId, String userId);

    void addRoleUser(UserRole userRole);

    void addRoles(List<Role> roles);

    void addRolePermissions(List<RolePermission> rolePermissions);

    List<RoleDTO> getRoleList(QueryRequest queryRequest);

    List<User> getRoleUserList(QueryRequest queryRequest);

    void deleteRoleUser(String roleId, String userId);
}