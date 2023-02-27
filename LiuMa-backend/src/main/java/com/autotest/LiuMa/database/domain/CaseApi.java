package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class CaseApi implements Serializable {
    private String id;

    private Long index;

    private String caseId;

    private String apiId;

    private String description;

    private String header;

    private String body;

    private String query;

    private String rest;

    private String assertion;

    private String relation;

    private String controller;

}