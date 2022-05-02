package com.autotest.LiuMa.service;

import com.autotest.LiuMa.database.domain.User;
import com.autotest.LiuMa.database.domain.UserRole;
import com.autotest.LiuMa.database.mapper.RoleMapper;
import com.autotest.LiuMa.dto.RoleDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class RoleService {

    @Resource
    private RoleMapper roleMapper;

    public List<RoleDTO> getRoleList(QueryRequest request){
        if(request.getCondition() != null && !request.getCondition().equals("")){
            request.setCondition("%"+request.getCondition()+"%");
        }
        return roleMapper.getRoleList(request);
    }

    public List<User> getRoleUserList(QueryRequest request){
        if(request.getCondition() != null && !request.getCondition().equals("")){
            request.setCondition("%"+request.getCondition()+"%");
        }
        return roleMapper.getRoleUserList(request);
    }

    public void deleteRoleUser(UserRole userRole){
        roleMapper.deleteRoleUser(userRole.getRoleId(), userRole.getUserId());
    }
}
