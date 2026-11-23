package top.lightlink.web.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.lightlink.common.Constants;
import top.lightlink.components.RedisComponent;
import top.lightlink.entity.dto.UserInfoDto;
import top.lightlink.entity.enums.ResponseCodeEnum;
import top.lightlink.entity.po.ApplyInfo;
import top.lightlink.entity.vo.ResponseVO;
import top.lightlink.exception.BusinessException;
import top.lightlink.service.AccountService;
import top.lightlink.utils.RedisUtil;

@RestController
@RequestMapping("/account")
@Validated
public class AccountController extends ABaseController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private RedisComponent redisComponent;
    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("/applyAccount")
    public ResponseVO applyAccount(
            @NotEmpty @Email String email,
            @NotEmpty String name,
            String info,
            @NotEmpty @Size(min = 8, max = 200) String password
    ){
        ApplyInfo applyInfo = new ApplyInfo();
        applyInfo.setEmail(email);
        applyInfo.setName(name);
        applyInfo.setInfo(info);
        applyInfo.setPassword(password);

        accountService.applyAccount(applyInfo);

        return getSuccessResponse(null);
    }

    @RequestMapping("/login")
    public ResponseVO login(
            HttpServletResponse response,
            @NotEmpty String nameOrEmail,
            @NotEmpty String password){
        UserInfoDto tokenUserInfoDto = accountService.login(nameOrEmail, password);
        saveToken2Cookie(response, tokenUserInfoDto.getToken());
        return getSuccessResponse(tokenUserInfoDto);
    }

    @RequestMapping("/autoLogin")
    public ResponseVO autoLogin(HttpServletResponse response){
        UserInfoDto tokenUserInfoDto = getUserInfoDto();
        if(tokenUserInfoDto == null){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        saveToken2Cookie(response, tokenUserInfoDto.getToken());
        return getSuccessResponse(tokenUserInfoDto);
    }

    @RequestMapping("/logout")
    public ResponseVO logout(HttpServletResponse response){
        UserInfoDto tokenUserInfoDto = getUserInfoDto();
        if(tokenUserInfoDto == null){
            return getSuccessResponse(null);
        }
        System.out.println("logoutInfo: " + tokenUserInfoDto.getUid() + " " + tokenUserInfoDto.getToken());
        redisComponent.removeUserToken(tokenUserInfoDto.getUid());
        Cookie cookie = new Cookie(Constants.TOKEN_USER, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return getSuccessResponse(null);
    }
}