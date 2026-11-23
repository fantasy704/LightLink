package top.lightlink.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.lightlink.common.Constants;
import top.lightlink.entity.dto.UserInfoDto;
import top.lightlink.entity.po.UserInfo;
import top.lightlink.utils.CopyUtil;
import top.lightlink.utils.RedisUtil;

import java.util.UUID;

@Component
public class RedisComponent {
    @Autowired
    private RedisUtil redisUtil;

    public UserInfoDto saveUserInfoDto(UserInfo userInfo){
        UserInfoDto dto = new UserInfoDto();
        CopyUtil.copyProperties(userInfo, dto);
        String token = UUID.randomUUID().toString();
        dto.setToken(token);
        redisUtil.setex(Constants.REDIS_KEY_UID_TOKEN + userInfo.getUid(), token, Constants.LOGIN_USER_EXPIRE);
        redisUtil.setex(Constants.REDIS_KEY_LOGIN_USER + token, dto, Constants.LOGIN_USER_EXPIRE);
        return dto;
    }

    public UserInfoDto getUserInfoDto(String token){
        return (UserInfoDto) redisUtil.get(Constants.REDIS_KEY_LOGIN_USER + token);
    }

    public boolean isUserOnline(String uid){
        return redisUtil.keyExists(Constants.REDIS_KEY_UID_TOKEN + uid);
    }

    public void removeUserToken(String uid){
        String token = (String) redisUtil.get(Constants.REDIS_KEY_UID_TOKEN + uid);
        if (token != null) {
            redisUtil.delete(Constants.REDIS_KEY_LOGIN_USER + token);
            redisUtil.delete(Constants.REDIS_KEY_UID_TOKEN + uid);
        }
    }
}