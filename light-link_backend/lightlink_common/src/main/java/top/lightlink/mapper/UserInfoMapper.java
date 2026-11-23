package top.lightlink.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.lightlink.entity.po.UserInfo;
import top.lightlink.entity.query.UserInfoQuery;

import java.util.List;

@Mapper
public interface UserInfoMapper {
    int insert(UserInfo userInfo);
    UserInfo selectByParam(UserInfoQuery query);
    List<UserInfo> selectListByParam(UserInfoQuery query);
}
