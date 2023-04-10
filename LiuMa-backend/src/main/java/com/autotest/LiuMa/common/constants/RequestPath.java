package com.autotest.LiuMa.common.constants;

public enum RequestPath {
    
    LOGIN_PATH("^/autotest/login$"),
    REGISTER_PATH("^/autotest/register$"),
    ENGINE_TOKEN_PATH("^/openapi/engine/token/apply$"),
    SCREENSHOT_PATH("^/openapi/screenshot/.+$"),
    DOWNLOAD_PATH("^/openapi/download/.+$"),
    RUN_PATH("^/openapi/exec/.+$"),
    ENGINE_PATH("^/openapi/.+$");

    public String path;

    RequestPath(String path) {
        this.path = path;
    }

}
