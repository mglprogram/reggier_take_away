package com.miao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.miao.common.Result;
import com.miao.pojo.User;
import com.miao.service.UserService;
import com.miao.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户信息 前端控制器
 * </p>
 *
 * @author 缪广亮
 * @since 2024-11-16
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 手机发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public Result sendMsg(@RequestBody User user, HttpSession session) {
//        获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
//          随机生成四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code:{}",code);
//            将发送的验证码保存到session
//            session.setAttribute(phone,code);
//            将生成的验证码缓存到redis中，有效期为5分钟
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
            return Result.success("手机验证码发送成功");
        }
        return Result.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map map, HttpSession session) {
        log.info("map:{}",map);
//        获取手机号
        String phone = map.get("phone").toString();
//        获取验证码
        String code = map.get("code").toString();
//        从session中获取保存的验证码
//        Object codeInSession = session.getAttribute(phone);
//        从redis中获取保存的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);
//        进行验证码比对（页面提交的验证码和session中保存的验证码比对）
        if (codeInSession != null && codeInSession.equals(code)){
//            若比对成功，说明登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
//            手机号是唯一标识
            User user = userService.getOne(queryWrapper);
//            判断当前手机号对应的用户是否为新用户
            if (user == null){
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
//            为filter保存的session
            session.setAttribute("user",user.getId());
//            如果用户登录成功，删除redis中缓存的验证码
            redisTemplate.delete(phone);
            return Result.success(user);
        }
        return Result.error("登录失败");
    }

}
