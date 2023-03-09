package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.Device;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceDTO extends Device {

    private String username;
}
