package top.lightlink.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.lightlink.entity.po.ServerMember;
import top.lightlink.entity.query.ServerMemberQuery;

import java.util.List;

@Mapper
public interface ServerMemberMapper {
    int insert(ServerMember serverMember);
    ServerMember selectByParam(ServerMemberQuery query);
    List<ServerMember> selectListByParam(ServerMemberQuery query);
    int selectCountByParam(ServerMemberQuery query);
    int deleteByParam(ServerMemberQuery query);
}
