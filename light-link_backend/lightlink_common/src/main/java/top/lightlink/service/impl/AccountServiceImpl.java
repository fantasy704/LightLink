package top.lightlink.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.lightlink.components.RedisComponent;
import top.lightlink.entity.dto.UserInfoDto;
import top.lightlink.entity.enums.ResponseCodeEnum;
import top.lightlink.entity.po.ApplyInfo;
import top.lightlink.entity.po.UserInfo;
import top.lightlink.entity.query.ApplyInfoQuery;
import top.lightlink.entity.query.UserInfoQuery;
import top.lightlink.exception.BusinessException;
import top.lightlink.mapper.ApplyInfoMapper;
import top.lightlink.mapper.UserInfoMapper;
import top.lightlink.service.AccountService;
import top.lightlink.utils.CopyUtil;

import java.util.List;
import java.util.Random;

@Service("accountService")
public class AccountServiceImpl implements AccountService {
    @Autowired
    private ApplyInfoMapper applyInfoMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private RedisComponent redisComponent;

    /**
     * 申请账号
     *
     * @param applyInfo 申请信息
     */
    @Override
    public void applyAccount(ApplyInfo applyInfo) {
        ApplyInfoQuery query = new ApplyInfoQuery();
        query.setEmail(applyInfo.getEmail());
        int total = applyInfoMapper.selectCountByParam(query);
        if(total > 0){
            throw new BusinessException(ResponseCodeEnum.CODE_601);
        }
        applyInfoMapper.insert(applyInfo);
    }

    @Override
    @Transactional
    public void passAccount(String email, Boolean passed) {
        ApplyInfoQuery applyInfoQuery = new ApplyInfoQuery();
        applyInfoQuery.setEmail(email);
        ApplyInfo applyInfo = applyInfoMapper.selectByParam(applyInfoQuery);
        if(applyInfo == null){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if(!passed){
            //TODO 邮件通知用户被拒绝
            System.out.println("拒绝");
            applyInfoMapper.deleteByParam(applyInfoQuery);
            return;
        }
        UserInfo userInfo = new UserInfo();
        CopyUtil.copyProperties(applyInfo, userInfo);
        String uid = "U" + (10000000 + new Random().nextInt(89999999));
        userInfo.setUid(uid);
        userInfoMapper.insert(userInfo);
        applyInfoMapper.deleteByParam(applyInfoQuery);
    }

    @Override
    public UserInfoDto login(String nameOrEmail, String password) {
        UserInfoQuery query = new UserInfoQuery();
        query.setEmailOrName(nameOrEmail);
        UserInfo userInfo = userInfoMapper.selectByParam(query);
        if(userInfo == null || !userInfo.getPassword().equals(password)){
            throw new BusinessException(403 ,"账号或密码错误");
        }
        if(redisComponent.isUserOnline(userInfo.getUid())){
            throw new BusinessException(ResponseCodeEnum.CODE_902);
        }
        return redisComponent.saveUserInfoDto(userInfo);
    }

    @Override
    public List<ApplyInfo> loadApplyList() {
        return applyInfoMapper.selectListByParam(null);
    }
}