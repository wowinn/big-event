package com.yangzhou.service.impl;

import com.yangzhou.pojo.Email;
import com.yangzhou.service.EmailService;
import com.yangzhou.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private Email email;

    /**
     * @param to 收件人邮箱
     * @param title 邮件标题
     * @param content 邮件正文
     * @return
     */
    @Override
    public boolean send(String to, String title, String content) {
        //发送邮件
        return EmailUtil.sendMail(email,to, title, content);
    }
}
