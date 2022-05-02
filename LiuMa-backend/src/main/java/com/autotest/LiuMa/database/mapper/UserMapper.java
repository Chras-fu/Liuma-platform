package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.User;
import com.autotest.LiuMa.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    UserDTO getUserInfo(String id);

    UserDTO getUser(String account);

    List<User> getAllUser();

    List<User> queryUser(String account);

    List<String> getUserRoleList(String projectId, String userId);

    void updateProject(String id, String projectId);

    void updatePassword(String id, String password);

    void updateInfo(User user);

    void addUser(User user);
}