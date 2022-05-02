package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class CollectionCase implements Serializable {
    private String id;

    private Long index;

    private String collectionId;

    private String caseId;

}