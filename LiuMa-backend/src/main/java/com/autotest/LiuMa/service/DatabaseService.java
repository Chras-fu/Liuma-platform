package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.common.exception.DuplicateException;
import com.autotest.LiuMa.database.domain.Database;
import com.autotest.LiuMa.database.mapper.DatabaseMapper;
import com.autotest.LiuMa.dto.DBConnectInfo;
import com.autotest.LiuMa.dto.DatabaseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class DatabaseService {

    @Resource
    private DatabaseMapper databaseMapper;

    public void saveDatabase(DatabaseDTO database) {
        Database oldDatabase = databaseMapper.getDatabaseByName(database.getEnvironmentId(), database.getDatabaseKey());
        if(oldDatabase != null && !Objects.equals(oldDatabase.getId(), database.getId())){
            throw new DuplicateException("当前环境已有重名数据库");
        }
        if(database.getId() == null || database.getId().equals("")){
            //新增数据库
            database.setId(UUID.randomUUID().toString());
            database.setConnectInfo(JSONObject.toJSONString(database.getInfo()));
            database.setCreateTime(System.currentTimeMillis());
            database.setUpdateTime(System.currentTimeMillis());
            database.setCreateUser(database.getUpdateUser());
        }else{
            // 更新数据库
            database.setUpdateTime(System.currentTimeMillis());
            database.setConnectInfo(JSONObject.toJSONString(database.getInfo()));
        }
        databaseMapper.saveDatabase(database);
    }

    public void deleteDatabase(Database database) {
        databaseMapper.deleteDatabase(database.getId());
    }

    public List<String> getDatabaseNameList(String projectId){
        return databaseMapper.getDatabaseNameList(projectId);
    }

    public List<DatabaseDTO> getDatabaseList(String environmentId){
        List<DatabaseDTO> databaseDTOS = databaseMapper.getDatabaseList(environmentId);
        for(DatabaseDTO databaseDTO: databaseDTOS){
            DBConnectInfo info = JSONObject.parseObject(databaseDTO.getConnectInfo(), DBConnectInfo.class);
            databaseDTO.setInfo(info);
        }
        return databaseMapper.getDatabaseList(environmentId);
    }

}
