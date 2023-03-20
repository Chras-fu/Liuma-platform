package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Driver;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DriverMapper {

    Driver getDriverByName(String projectId, String name);

    Driver getDriverById(String id);

    void saveDriver(Driver driver);

    void deleteDriver(String id);

    List<Driver> getDriverList(String projectId, String condition);
}
