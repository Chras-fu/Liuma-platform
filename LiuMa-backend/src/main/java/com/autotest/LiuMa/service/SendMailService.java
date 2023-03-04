package com.autotest.LiuMa.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class SendMailService {

    @Resource
    private JavaMailSender javaMailSender;

    public void sendReportMail(String from, String to, String title, String content) throws MessagingException {
        //利用这个类来配置发送邮件的信息
        MimeMessage message = javaMailSender.createMimeMessage();
        //true表示需要创建一个multipart message
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(title);
        helper.setText(content, true);
        //利用这个方法发送邮件
        javaMailSender.send(message);
    }

}
