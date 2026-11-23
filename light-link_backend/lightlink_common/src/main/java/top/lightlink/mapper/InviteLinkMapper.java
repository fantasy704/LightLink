package top.lightlink.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.lightlink.entity.po.InviteLink;
import top.lightlink.entity.query.InviteLinkQuery;

@Mapper
public interface InviteLinkMapper {
    int insert(InviteLink inviteLink);
    InviteLink selectByParam(InviteLinkQuery query);
    int deleteByParam(InviteLinkQuery query);
}
