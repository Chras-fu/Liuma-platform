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

    public static void sendMail(String receiver, String title, String content, String accessKey, String accessSecret,
                                String runnerSenderAddress, String runnerSenderName) {
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