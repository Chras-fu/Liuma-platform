package com.autotest.LiuMa.common.utils;

import com.qiniu.util.*;
import okhttp3.*;


public class UploadUtils {

    private static final String ak = "*****";   // 七牛云ak
    private static final String sk = "*****";    // 七牛云sk
    private static final Auth auth = Auth.create(ak, sk);
    private static final String imageBucket = "*****";    // 七牛云空间名
    private static final String downloadUrl = "*****";  // 七牛云加速域名
    private static final String uploadUrl = "http://upload-cn-east-2.qiniup.com";

    public static String getUpToken(String bucketName) {
        return auth.uploadToken(bucketName, null, 3600, new StringMap().put("insertOnly", 1));
    }

    public static void uploadImageB64(String fileName, String file64) throws Exception {
        String url = uploadUrl + "/putb64/-1/key/"+ UrlSafeBase64.encodeToString(fileName);
        RequestBody body = RequestBody.create(null, file64);
        Request request = new Request.Builder().url(url).
                addHeader("Content-Type", "application/octet-stream").
                addHeader("Authorization", "UpToken " + getUpToken(imageBucket)).
                post(body).build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).execute();
    }

    public static String getDownloadUrl(String fileName){
        return downloadUrl + "/" + fileName;
    }

}
