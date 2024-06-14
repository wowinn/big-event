package com.yangzhou.pojo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "email")
@Data
public class Email {
    @Value("${email.user}")
    //发件人邮箱
    public String user ;

    @Value("${email.code}")
    //发件人邮箱授权码
    public String code ;

    @Value("${email.host}")
    //发件人邮箱对应的服务器域名,如果是163邮箱:smtp.163.com   qq邮箱: smtp.qq.com
    public String host ;

    @Value("${email.auth}")
    //身份验证开关
    private boolean auth ;

}
