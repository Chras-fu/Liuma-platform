package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.constants.PermissionEnum;
import com.autotest.LiuMa.common.exception.LMException;
import com.autotest.LiuMa.database.domain.*;
import com.autotest.LiuMa.database.mapper.CommonParamMapper;
import com.autotest.LiuMa.database.mapper.ProjectMapper;
import com.autotest.LiuMa.database.mapper.RoleMapper;
import com.autotest.LiuMa.database.mapper.UserMapper;
import com.autotest.LiuMa.dto.PlanDTO;
import com.autotest.LiuMa.dto.ProjectDTO;
import com.autotest.LiuMa.request.ProjectUserRequest;
import com.autotest.LiuMa.request.QueryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProjectService {

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private CommonParamMapper commonParamMapper;

    @Resource
    private UserMapper userMapper;

    public List<Project> getUserProject(String userId) {
        return projectMapper.getUserProject(userId);
    }

    public Project getProjectInfo(String projectId) {
        return projectMapper.getProjectById(projectId);
    }

    public List<ProjectDTO> getProjectList(QueryRequest request){
        if(request.getCondition() != null && !request.getCondition().equals("")){
            request.setCondition("%"+request.getCondition()+"%");
        }
        return projectMapper.getProjectList(request);
    }

    public List<User> getProjectUserList(QueryRequest request){
        if(request.getCondition() != null && !request.getCondition().equals("")){
            request.setCondition("%"+request.getCondition()+"%");
        }
        return projectMapper.getProjectUserList(request);
    }

    public List<Role> getProjectRoleList(String projectId) {
        return projectMapper.getProjectRoleList(projectId);
    }

    public void saveProject(Project project){
        Project oldProject = projectMapper.getProjectByName(project.getName());
        if(oldProject != null){
            throw new LMException("项目重名");
        }
        // 新增项目
        project.setId(UUID.randomUUID().toString());
        project.setCreateTime(System.currentTimeMillis());
        project.setUpdateTime(System.currentTimeMillis());
        project.setStatus("Normal");
        projectMapper.addProject(project);
        // 新增项目角色
        List<Role> roles = new ArrayList<>();
        List<String> roleNames = new ArrayList<>();
        roleNames.add("项目管理员");
        roleNames.add("项目普通用户");
        for(String roleName:roleNames){
            Role role = new Role();
            role.setId(UUID.randomUUID().toString());
            role.setName(roleName);
            role.setProjectId(project.getId());
            role.setCreateTime(System.currentTimeMillis());
            role.setUpdateTime(System.currentTimeMillis());
            roles.add(role);
        }
        roleMapper.addRoles(roles);
        // 新增角色权限
        List<RolePermission> rolePermissions = new ArrayList<>();
        for(PermissionEnum permissionEnum: PermissionEnum.values()){
            if(permissionEnum != PermissionEnum.PROJECT_MENU){
                RolePermission adminRolePermission = new RolePermission();
                adminRolePermission.setId(UUID.randomUUID().toString());
                adminRolePermission.setCreateTime(System.currentTimeMillis());
                adminRolePermission.setUpdateTime(System.currentTimeMillis());
                adminRolePermission.setPermissionId(permissionEnum.id);
                if(permissionEnum == PermissionEnum.NORMAL_MENU || permissionEnum == PermissionEnum.SETTING_MENU){
                    RolePermission commonRolePermission = new RolePermission();
                    commonRolePermission.setId(UUID.randomUUID().toString());
                    commonRolePermission.setCreateTime(System.currentTimeMillis());
                    commonRolePermission.setUpdateTime(System.currentTimeMillis());
                    commonRolePermission.setPermissionId(permissionEnum.id);
                    commonRolePermission.setRoleId(roles.get(1).getId());
                    rolePermissions.add(commonRolePermission);
                }
                adminRolePermission.setRoleId(roles.get(0).getId());
                rolePermissions.add(adminRolePermission);
            }
        }
        roleMapper.addRolePermissions(rolePermissions);
        // 新增用户角色
        UserRole userRole = new UserRole();
        userRole.setId(UUID.randomUUID().toString());
        userRole.setUserId(project.getProjectAdmin());
        userRole.setRoleId(roles.get(0).getId());
        userRole.setCreateTime(System.currentTimeMillis());
        userRole.setUpdateTime(System.currentTimeMillis());
        roleMapper.addRoleUser(userRole);
        // 新增用户项目
        UserProject userProject = new UserProject();
        userProject.setId(UUID.randomUUID().toString());
        userProject.setUserId(project.getProjectAdmin());
        userProject.setProjectId(project.getId());
        userProject.setCreateTime(System.currentTimeMillis());
        userProject.setUpdateTime(System.currentTimeMillis());
        projectMapper.addProjectUser(userProject);
        // 新增公共参数组
        List<ParamGroup> paramGroups = new ArrayList<>();
        paramGroups.add(this.getParamGroup(project.getId(), "Header", "接口请求头参数组", "system"));
        paramGroups.add(this.getParamGroup(project.getId(), "Proxy", "接口请求代理参数组", "system"));
        paramGroups.add(this.getParamGroup(project.getId(), "Custom", "自定义参数组", "custom"));
        commonParamMapper.insertParamGroup(paramGroups);
    }

    private ParamGroup getParamGroup(String projectId, String name, String desc, String type){
        ParamGroup paramGroup = new ParamGroup();
        paramGroup.setId(UUID.randomUUID().toString());
        paramGroup.setProjectId(projectId);
        paramGroup.setName(name);
        paramGroup.setDescription(desc);
        paramGroup.setParamType(type);
        paramGroup.setCreateTime(System.currentTimeMillis());
        paramGroup.setUpdateTime(System.currentTimeMillis());
        paramGroup.setCreateUser("system_admin_user");
        paramGroup.setUpdateUser("system_admin_user");
        return paramGroup;
    }

    public void saveProjectUser(ProjectUserRequest request){
        for(String userId: request.getUserIds()){
            if(projectMapper.getProjectUser(request.getProjectId(), userId) == null){
                UserProject userProject = new UserProject();
                userProject.setId(UUID.randomUUID().toString());
                userProject.setUserId(userId);
                userProject.setProjectId(request.getProjectId());
                userProject.setCreateTime(System.currentTimeMillis());
                userProject.setUpdateTime(System.currentTimeMillis());
                projectMapper.addProjectUser(userProject);  // 新增项目用户
            }
            if(!request.getIsEdit()) {  // 新增项目用户
                for (String roleId : request.getRoleIds()) {
                    if (roleMapper.getRoleUser(roleId, userId) != null) {
                        continue;
                    }
                    UserRole userRole = new UserRole();
                    userRole.setId(UUID.randomUUID().toString());
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    userRole.setCreateTime(System.currentTimeMillis());
                    userRole.setUpdateTime(System.currentTimeMillis());
                    roleMapper.addRoleUser(userRole);   // 新增项目用户角色
                }
            }else {  // 编辑项目用户
                List<String> oldRoleIds = userMapper.getUserRoleList(request.getProjectId(), userId);
                for(String newRoleId: request.getRoleIds()){
                    if(oldRoleIds.contains(newRoleId)){
                        oldRoleIds.remove(newRoleId);
                        continue;   // 已有的用户角色不做更改
                    }
                    UserRole userRole = new UserRole();
                    userRole.setId(UUID.randomUUID().toString());
                    userRole.setUserId(userId);
                    userRole.setRoleId(newRoleId);
                    userRole.setCreateTime(System.currentTimeMillis());
                    userRole.setUpdateTime(System.currentTimeMillis());
                    roleMapper.addRoleUser(userRole);   // 新增项目用户角色
                }
                for(String oldRoleId: oldRoleIds){
                    roleMapper.deleteRoleUser(oldRoleId, userId);   // 删除项目角色
                }
            }
        }
    }

    public void deleteProjectUser(String projectId, String userId){
        projectMapper.deleteProjectUser(projectId, userId); // 删除项目用户
        List<String> roleIds = userMapper.getUserRoleList(projectId, userId);
        for(String roleId: roleIds){
            roleMapper.deleteRoleUser(roleId, userId);   // 删除项目角色
        }
    }

    public void deleteProject(String projectId){
        projectMapper.deleteProject(projectId);
    }

    public void recoverProject(String projectId){
        projectMapper.recoverProject(projectId);
    }

}
