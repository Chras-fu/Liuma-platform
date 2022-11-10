package com.autotest.LiuMa.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class JsonSchemaItemDTO {

    @JSONField(name = "$id")
    private String id;
    private String title;
    private String type;
    private String description;
    private List<JsonSchemaItemDTO> items;
    private Map<String, Object> mock;
    private Map<String, JsonSchemaItemDTO> properties;
    private List<String> required ;

    @JSONField(name = "$schema")
    private String schema;


    public JsonSchemaItemDTO() {
        this.mock = new LinkedHashMap<>();
        this.mock.put("mock", "");
    }

    public JsonSchemaItemDTO(String type) {
        this.type = type;
        this.initParam(type);
    }

    public void setType(String type) {
        this.type = type;
        this.initParam(type);
    }

    private void initParam(String type) {
        if (type.equals("object")) {
            this.properties = new LinkedHashMap<>();
        } else if (type.equals("array")) {
            this.items = new LinkedList<>();
        }
    }
}
