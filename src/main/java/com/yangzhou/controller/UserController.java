package com.yangzhou.controller;

import com.yangzhou.pojo.Result;
import com.yangzhou.pojo.User;
import com.yangzhou.service.EmailService;
import com.yangzhou.service.UserService;
import com.yangzhou.utils.JwtUtil;
import com.yangzhou.utils.Md5Util;
import com.yangzhou.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.yangzhou.utils.CodeUtil.generateCode;
import static com.yangzhou.utils.EmailUtil.sendMail;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$") String username, @Email String email, @Pattern(regexp = "^\\S{5,16}$") String password) {
        //查询用户
        User u = userService.findByUserName(username);
        if (u == null) {
            //注册
            userService.register(username, email, password);
            return Result.success();
        } else {
            //占用
            return Result.error("用户名已被占用");
        }
    }

    @PostMapping("/login")
    public Result<String> login(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password) {
        //根据用户名查询用户
        User loginUser = userService.findByUserName(username);
        //判断该用户是否存在
        if (loginUser == null) {
            return Result.error("用户名错误");
        }
        //判断密码是否正确  password密文
        if (Md5Util.getMD5String(password).equals(loginUser.getPassword())) {
            //登录成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", loginUser.getId());
            claims.put("username", loginUser.getUsername());
            String token = JwtUtil.genToken(claims);
            //把token存储到redis中
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token, token, 1, TimeUnit.HOURS);
            return Result.success(token);
        }
        return Result.error("密码错误");
    }

    @GetMapping("/userInfo")
    public Result<User> userInfo() {
        Map<String, Object> map = ThreadLocalUtil.get();
        User user = userService.findByUserName((String)map.get("username"));
        return Result.success(user);
    }

    @PutMapping("/update")
    //RequestBody json转为为对象
    public Result update(@RequestBody @Validated User user) {
        userService.update(user);
        return Result.success();
    }

    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL @NotEmpty String avatarUrl) {
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }

    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String, String> params, @RequestHeader("Authorization") String token) {
        //校验参数
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");
        if (!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)) {
            return Result.error("缺少必要的参数");
        }
        Map<String, Object> map = ThreadLocalUtil.get();
        User user = userService.findByUserName((String)map.get("username"));
        if (!user.getPassword().equals(Md5Util.getMD5String(oldPwd))) {
            return Result.error("原密码填写不正确");
        }
        if (!newPwd.equals(rePwd)) {
            return Result.error("两次填写的新密码不一样");
        }
        userService.updatePwd(newPwd);
        //删除redis中对应的token
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);
        return Result.success();
    }

    @GetMapping("/findEmail")
    public Result findPwd(@Email(message = "邮箱地址不合法") String email) {
        User u = userService.findByUserEmail(email);
        if(u == null) {
            return Result.error(email + "未注册");
        }
        //查找到注册邮箱， 发送邮箱验证码
        String code = generateCode();

        //邮件标题
        String title = "大事件 密码找回";
        //邮件正文
        String content = "<html><body><h3>您的邮箱验证码位：</h3><span>" + code + "</span><br><span>注意：邮箱验证码有效期为10分钟</span></body></html>";
        //发送邮件
        if(emailService.send(email, title, content)) {
            //把邮箱和验证码存储到redis中
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(email, code, 10, TimeUnit.MINUTES);
            return Result.success();
        }
        return Result.error("发送邮箱验证码错误");
    }

    @PostMapping("/resetPwd")
    public Result resetPwd(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        String code = params.get("code");
        String newPassword = params.get("newPassword");
        String rePassword = params.get("rePassword");

        if (!StringUtils.hasLength(newPassword) || !StringUtils.hasLength(rePassword)) {
            return Result.error("缺少必要的参数");
        }
        if (!newPassword.equals(rePassword)) {
            return Result.error("两次填写的新密码不一样");
        }

        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String localCode = operations.get(email);
        if(localCode == null) {
            return Result.error("邮箱验证码已失效");
        }
        if(!localCode.equals(code)) {
            return Result.error("邮箱验证码错误");
        }
        userService.resetPwd(email, newPassword);
        operations.getOperations().delete(email);
        return Result.success();
    }
}
