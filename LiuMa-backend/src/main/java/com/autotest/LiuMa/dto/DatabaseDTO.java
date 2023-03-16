package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.Database;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DatabaseDTO extends Database {

    private DBConnectInfo info;

}
