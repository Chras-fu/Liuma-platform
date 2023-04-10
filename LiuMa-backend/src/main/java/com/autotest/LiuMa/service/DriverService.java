package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.exception.DuplicateException;
import com.autotest.LiuMa.database.domain.Driver;
import com.autotest.LiuMa.database.mapper.DriverMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class DriverService {

    @Resource
    private DriverMapper driverMapper;

    public void saveDriver(Driver driver) {
        Driver oldDriver = driverMapper.getDriverByName(driver.getProjectId(), driver.getName());
        if(oldDriver != null && !Objects.equals(oldDriver.getId(), driver.getId())){
            throw new DuplicateException("当前项目已有重名驱动配置");
        }
        if(driver.getId() == null || driver.getId().equals("")){
            //新增版本
            driver.setId(UUID.randomUUID().toString());
            driver.setCreateTime(System.currentTimeMillis());
            driver.setUpdateTime(System.currentTimeMillis());
        }else{
            // 更新版本
            driver.setUpdateTime(System.currentTimeMillis());
        }
        driverMapper.saveDriver(driver);
    }

    public void deleteDriver(String id) {
        driverMapper.deleteDriver(id);
    }

    public List<Driver> getDriverList(String projectId, String condition) {
        if(condition != null && !condition.equals("")){
            condition = "%"+condition+"%";
        }
        return driverMapper.getDriverList(projectId, condition);
    }

}
