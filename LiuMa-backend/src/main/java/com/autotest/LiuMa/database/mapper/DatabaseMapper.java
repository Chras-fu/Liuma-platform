package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Database;
import com.autotest.LiuMa.dto.DatabaseDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DatabaseMapper {
    void saveDatabase(Database domain);

    void deleteDatabase(String id);

    Database getDatabaseByName(String environmentId, String databaseKey);

    List<String> getDatabaseNameList(String projectId);

    List<DatabaseDTO> getDatabaseList(String environmentId);

}