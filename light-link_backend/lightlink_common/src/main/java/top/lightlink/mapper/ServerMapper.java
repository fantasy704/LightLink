package top.lightlink.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.lightlink.entity.po.Server;
import top.lightlink.entity.query.ServerQuery;

import java.util.List;

@Mapper
public interface ServerMapper {
    int insert(Server server);
    List<Server> selectListByParam(ServerQuery query);
    Server selectByParam(ServerQuery query);
    int deleteByParam(ServerQuery query);
    int updateMaxNumber(Server server);
    int selectCountByParam(ServerQuery query);
}
