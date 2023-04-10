package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.exception.PwdVerifyException;
import com.autotest.LiuMa.database.domain.*;
import com.autotest.LiuMa.database.mapper.*;
import com.autotest.LiuMa.dto.UserDTO;
import com.autotest.LiuMa.request.PasswordRequest;
import com.autotest.LiuMa.request.RegisterRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    public User getUserInfo(String id) {
        return userMapper.getUserInfo(id);
    }

    public UserDTO getUser(String account){
        return userMapper.getUser(account);
    }

    public List<String> getUserProjectPermission(String userId, String projectId){
        return permissionMapper.getUserPermissionByProject(projectId, userId);
    }

    public List<User> getAllUser(){
        return userMapper.getAllUser();
    }

    public List<User> queryUser(String account){
        return userMapper.queryUser(account);
    }

    public List<String> getUserRoleList(String projectId, String userId){
        return userMapper.getUserRoleList(projectId, userId);
    }

    public UserDTO switchProject(String id, String projectId) {
        userMapper.updateProject(id, projectId);
        UserDTO userDTO = userMapper.getUserInfo(id);
        userDTO.setPermissions(this.getUserProjectPermission(id, projectId));
        return userDTO;
    }

    public void updatePassword(PasswordRequest request) {
        User user = userMapper.getUserInfo(request.getUserId());
        if(!user.getPassword().equals(request.getOldPassword())){
            throw new PwdVerifyException("旧密码输入错误");
        }
        userMapper.updatePassword(request.getUserId(), request.getNewPassword());
    }

    public void updateInfo(User user) {
        user.setUpdateTime(System.currentTimeMillis());
        userMapper.updateInfo(user);
    }

    public String registerUser(RegisterRequest request){
        User oldAccountUser = userMapper.getUser(request.getAccount());
        if(oldAccountUser != null){
            return "该登录账号已被注册!";
        }
        User oldMobileUser = userMapper.getUser(request.getMobile().toString());
        if(oldMobileUser != null){
            return "该手机号已被注册!";
        }
        Project project = projectMapper.getProjectByName("演示项目");
        Role role = roleMapper.getRoleByProjectAndName(project.getId(), "项目普通用户");
        // 新建用户
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(request.getUsername());
        user.setAccount(request.getAccount());
        user.setMobile(request.getMobile());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(System.currentTimeMillis());
        user.setLastProject(project.getId());
        userMapper.addUser(user);
        // 添加用户项目
        UserProject userProject = new UserProject();
        userProject.setId(UUID.randomUUID().toString());
        userProject.setUserId(user.getId());
        userProject.setProjectId(project.getId());
        userProject.setCreateTime(System.currentTimeMillis());
        userProject.setUpdateTime(System.currentTimeMillis());
        projectMapper.addProjectUser(userProject);
        // 添加用户角色
        UserRole userRole = new UserRole();
        userRole.setId(UUID.randomUUID().toString());
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        userRole.setCreateTime(System.currentTimeMillis());
        userRole.setUpdateTime(System.currentTimeMillis());
        roleMapper.addRoleUser(userRole);
        return "注册成功";
    }
}
