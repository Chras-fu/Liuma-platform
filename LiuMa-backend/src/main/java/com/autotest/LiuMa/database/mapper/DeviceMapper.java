package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Device;
import com.autotest.LiuMa.dto.DeviceDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeviceMapper {
    void addDevice(Device device);

    void updateDevice(Device device);

    void updateDeviceByAgent(String agent);

    List<Device> selectTimeoutDevice();

    List<DeviceDTO> getDeviceList(String projectId, String owner, String condition, String status, List<String> brand,
                                  List<String> android, List<String> apple, List<String> size);

    List<Device> getDeviceListBySystem(String projectId, String owner, String system);

    List<String> getDeviceFilter(String projectId, String owner, String field, String system);

    Device getDeviceBySerial(String serial);

    Device getDeviceById(String id);
}