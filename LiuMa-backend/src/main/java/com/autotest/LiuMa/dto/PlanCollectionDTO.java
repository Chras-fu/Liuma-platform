package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.PlanCollection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanCollectionDTO extends PlanCollection {
    private String collectionName;

    private String collectionVersion;
}
