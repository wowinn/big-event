package com.yangzhou.controller;

import com.yangzhou.pojo.Result;
import com.yangzhou.pojo.User;
import com.yangzhou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(String username, String password) {
        if (username != null && username.length() >= 5 && username.length() <= 16 &&
                password != null && password.length() >= 5 && password.length() <= 16) {
            //查询用户
            User u = userService.findByUserName(username);
            if (u == null) {
                //注册
                userService.register(username, password);
                return Result.success();
            } else {
                //占用
                return Result.error("用户名已被占用");
            }
        } else {
            return Result.error("参数不合法");
        }
    }
}
