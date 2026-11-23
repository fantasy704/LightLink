package top.lightlink.service;


import top.lightlink.entity.dto.UserInfoDto;
import top.lightlink.entity.po.ApplyInfo;

import java.util.List;

public interface AccountService {
    void applyAccount(ApplyInfo applyInfo);
    void passAccount(String applyInfo, Boolean passed);
    UserInfoDto login(String nameOrEmail, String password);
    List<ApplyInfo> loadApplyList();
}
