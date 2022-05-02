package com.autotest.LiuMa.dto;

import com.alibaba.fastjson.JSONArray;
import com.autotest.LiuMa.database.domain.Operation;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OperationDTO extends Operation {

    private String username;

    private JSONArray dataList;

    private JSONArray elementList;
}
