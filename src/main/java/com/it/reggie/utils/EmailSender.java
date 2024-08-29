package com.it.reggie.utils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Properties;
import java.util.Random;

@Component
public class EmailSender {
    /**
     *  private final JavaMailSender mailSender;
     *
     *     public EmailSender() {
     *         mailSender = createMailSender();
     *     }
     *     public void sendVerificationEmail(String toAddress, String verificationCode) {//邮件发送
     *         SimpleMailMessage message = new SimpleMailMessage();
     *         message.setTo(toAddress);
     *         message.setSubject("登录验证码");
     *         message.setText("记住你的验证码: " + verificationCode);
     *
     *         mailSender.send(message);
     *     }
     *     private JavaMailSender createMailSender() {//邮件配置
     *         JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
     *         mailSender.setHost("????");//设置 SMTP 服务器地址
     *         mailSender.setPort(?????); // 设置 SMTP 服务器的端口号
     *         mailSender.setUsername("??????");// 设置发送邮件的邮箱用户名
     *         mailSender.setPassword("??????");// 设置发送邮件的邮箱密码
     *         Properties props = mailSender.getJavaMailProperties();
     *         props.put("mail.smtp.auth", "true");
     *         props.put("mail.smtp.starttls.enable", "true");
     *         return mailSender;
     *     }
     */

    public static void senEmail(String email,String code)
    {
        try{
            SimpleEmail simpleEmail=new SimpleEmail();
            simpleEmail.setHostName("");//设置邮件服务器的主机名
            simpleEmail.setAuthentication("","");//设置发件人的邮箱和授权码进行身份验证
            simpleEmail.setFrom("","");//设置发件人的邮箱地址和名称
            simpleEmail.setSSLOnConnect(true);//启用 SSL 加密
            simpleEmail.addTo(email);//收件人的邮箱地址
            simpleEmail.setSubject("验证码");//设置邮件的主题
            simpleEmail.setMsg("登录邮箱验证码："+code);//设置邮件的内容
            simpleEmail.send(); // 发送邮件
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }


    public static String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;  // 生成六位数字验证码
        return String.valueOf(code);
    }

}
