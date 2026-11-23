package top.lightlink.web.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.lightlink.common.Constants;
import top.lightlink.components.RedisComponent;
import top.lightlink.entity.dto.UserInfoDto;
import top.lightlink.entity.enums.ResponseCodeEnum;
import top.lightlink.entity.vo.ResponseVO;
import top.lightlink.exception.BusinessException;

public class ABaseController {
    @Autowired
    private RedisComponent redisComponent;

    protected <T> ResponseVO<T> getSuccessResponse(T data) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setCode(200);
        responseVO.setMsg("success");
        responseVO.setData(data);
        return responseVO;
    }
    protected void saveToken2Cookie(HttpServletResponse response, String token){
        Cookie cookie = new Cookie(Constants.TOKEN_USER, token);
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    protected UserInfoDto getUserInfoDto(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader(Constants.TOKEN_USER);
        if(token == null){
            Cookie[] cookies = request.getCookies();
            if(cookies == null){
                return null;
            }
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(Constants.TOKEN_USER)){
                    token = cookie.getValue();
                }
            }
        }
        UserInfoDto dto = redisComponent.getUserInfoDto(token);
        if(dto == null){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        return dto;
    }
}
