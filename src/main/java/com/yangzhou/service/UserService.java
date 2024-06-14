package com.yangzhou.service;

import com.yangzhou.pojo.User;

public interface UserService {
    //根据用户名查询用户
    User findByUserName(String username);

    //注册
    void register(String username, String email, String password);

    //更新
    void update(User user);

    //更新头像
    void updateAvatar(String avatarUrl);

    //更新密码
    void updatePwd(String newPwd);

    //根据邮箱查找用户
    User findByUserEmail(String email);

    //重置密码
    void resetPwd(String email, String newPassword);
}
