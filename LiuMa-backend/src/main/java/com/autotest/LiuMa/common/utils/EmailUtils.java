package com.autotest.LiuMa.common.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

public class EmailUtils {

    private static final String accessKey = "*****";    // 阿里云邮件key
    private static final String accessSecret = "*****";     // 阿里云邮件secret
    private static final String runnerSenderAddress = "*****";  // 发送人邮箱地址
    private static final String runnerSenderName = "执行通知机器人";

    public static void sendMail(String receiver, String title, String content) {
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKey, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();
        try {
            request.setAccountName(runnerSenderAddress);
            request.setFromAlias(runnerSenderName);
            request.setAddressType(1);
            request.setReplyToAddress(false);
            request.setToAddress(receiver);
            request.setSubject(title);
            request.setHtmlBody(content);
            request.setMethod(MethodType.POST);

            client.getAcsResponse(request);
        } catch (ServerException e) {
            System.out.println("ErrCode : " + e.getErrCode());
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode : " + e.getErrCode());
            e.printStackTrace();
        }
    }

}