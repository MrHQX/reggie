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
     *         mailSender.setUsername("??????");// 设置发送邮件的邮箱用户名（通常是邮箱地址）
     *         mailSender.setPassword("??????");// 设置发送邮件的邮箱密码（或应用专用密码）
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
            simpleEmail.setHostName("smtp.163.com");
            simpleEmail.setAuthentication("18185352727@163.com","DNYBOVKHEYGJFIGM");
            simpleEmail.setFrom("18185352727@163.com","HQX");
            simpleEmail.setSSLOnConnect(true);
            simpleEmail.addTo(email);
            simpleEmail.setSubject("验证码");
            simpleEmail.setMsg("登录邮箱验证码："+code);
            simpleEmail.send();
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
