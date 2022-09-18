package com.autotest.LiuMa.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class NotificationRequest {
    private String id;

    private String projectId;

    private String type;

    private String name;

    private String webhookUrl;

    private String status;

    private JSONObject params;

    private String userId;
}
