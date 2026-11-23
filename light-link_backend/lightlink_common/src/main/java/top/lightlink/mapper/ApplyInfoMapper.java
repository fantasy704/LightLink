package top.lightlink.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.lightlink.entity.po.ApplyInfo;
import top.lightlink.entity.query.ApplyInfoQuery;

import java.util.List;

@Mapper
public interface ApplyInfoMapper {
    List<ApplyInfo> selectList(ApplyInfoQuery query);
    int insert(ApplyInfo item);
    int selectCountByParam(ApplyInfoQuery query);
    int deleteByParam(ApplyInfoQuery query);
    ApplyInfo selectByParam(ApplyInfoQuery query);
    List<ApplyInfo> selectListByParam(ApplyInfoQuery query);
}
