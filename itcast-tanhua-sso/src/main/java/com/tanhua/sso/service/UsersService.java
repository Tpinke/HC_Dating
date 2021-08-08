package com.tanhua.sso.service;

import com.tanhua.common.pojo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UsersService {

    @Autowired
    private UserService userService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean sendVerificationCode(String token) {
        User user = this.userService.queryUserByToken(token);
        Map<String, Object> sendCheckCode = this.smsService.sendCheckCode(user.getMobile());
        int code = ((Integer) sendCheckCode.get("code")).intValue();
        return code == 3;
    }

    public Boolean checkVerificationCode(String code, String token) {
        //查询到用户的手机号
        User user = this.userService.queryUserByToken(token);
        if (null == user) {
            return false;
        }

        String redisKey = SmsService.REDIS_KEY_PREFIX + user.getMobile();
        String value = this.redisTemplate.opsForValue().get(redisKey);

        if(StringUtils.equals(value, code)){
            //验证码正确
            this.redisTemplate.delete(redisKey);
            return true;
        }

        return false;
    }

    public boolean savePhone(String token, String newPhone) {
        User user = this.userService.queryUserByToken(token);
        return this.userService.updatePhone(user.getId(), newPhone);
    }
}
