package top.lightlink.admin.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.lightlink.admin.config.AppConfig;
import top.lightlink.entity.enums.PassedStatus;
import top.lightlink.entity.enums.ResponseCodeEnum;
import top.lightlink.entity.vo.ResponseVO;
import top.lightlink.exception.BusinessException;
import top.lightlink.service.AccountService;

@RestController
@RequestMapping("/user")
@Validated
public class UserController extends ABaseController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private AppConfig appConfig;

    @RequestMapping("/passAccount")
    public ResponseVO passAccount(@NotEmpty String email, @NotNull Integer status, @NotEmpty String adminKey){
        //TODO 管理员权限校验
        if(!adminKey.equals(appConfig.getAdminKey())){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        PassedStatus statusEnum = PassedStatus.getByCode(status);
        if(statusEnum == null){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        accountService.passAccount(email, statusEnum.getStatus());
        return getSuccessResponse(null);
    }

    @RequestMapping("/loadApplyList")
    public ResponseVO loadApplyList(@NotEmpty String adminKey){
        //TODO 管理员权限校验
        if(!adminKey.equals(appConfig.getAdminKey())){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        return getSuccessResponse(accountService.loadApplyList());
    }
}
