package com.autotest.LiuMa.common.utils;

import com.qiniu.util.*;
import okhttp3.*;


public class UploadUtils {

    public static String getUpToken(String bucketName,String ak, String sk) {
        Auth auth = Auth.create(ak, sk);
        return auth.uploadToken(bucketName, null, 3600, new StringMap().put("insertOnly", 1));
    }

    public static void uploadImageB64(String fileName, String file64, String uploadUrl,
                                      String imageBucket, String ak, String sk) throws Exception {
        String url = uploadUrl + "/putb64/-1/key/"+ UrlSafeBase64.encodeToString(fileName);
        RequestBody body = RequestBody.create(null, file64);
        Request request = new Request.Builder().url(url).
                addHeader("Content-Type", "application/octet-stream").
                addHeader("Authorization", "UpToken " + getUpToken(imageBucket, ak, sk)).
                post(body).build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).execute();
    }

}
