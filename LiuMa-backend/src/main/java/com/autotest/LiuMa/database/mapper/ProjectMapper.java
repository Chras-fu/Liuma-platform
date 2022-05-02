package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Project;
import com.autotest.LiuMa.database.domain.Role;
import com.autotest.LiuMa.database.domain.User;
import com.autotest.LiuMa.database.domain.UserProject;
import com.autotest.LiuMa.dto.ProjectDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMapper {
    List<Project> getUserProject(String userId);

    List<ProjectDTO> getProjectList(QueryRequest request);

    List<User> getProjectUserList(QueryRequest request);

    List<Role> getProjectRoleList(String projectId);

    List<String> getAllProjectId();

    Project getProjectByName(String name);

    Project getProjectById(String id);

    UserProject getProjectUser(String projectId, String userId);

    void addProjectUser(UserProject userProject);

    void addProject(Project project);

    void deleteProjectUser(String projectId, String userId);

    void deleteProject(String id);

    void recoverProject(String id);
}