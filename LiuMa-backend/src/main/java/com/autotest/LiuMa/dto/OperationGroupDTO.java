package com.autotest.LiuMa.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class OperationGroupDTO {
    private String id;

    private String name;

    private List<OperationDTO> operationList;
}
