package top.lightlink.service;

import top.lightlink.entity.po.Server;
import top.lightlink.entity.po.ServerMember;

import java.util.List;

public interface ServerService {
    List<Server> loadServerList();
    void addServer(String serverMaster, String serverName, String serverInfo, Integer maxNumber);
    void joinServer(String uid, String linkKey);
    List<ServerMember> loadSelfServerList(String uid);
    Boolean checkServerAndServerMember(String serverId, String uid);
    Server loadServerByLink(String ink);
    void deleteServer(String serverId, String uid);
    void editServerNumber(String uid, Integer maxNumber, String serverId);
    void removeServerMember(String uid, String serverId, String memberId);
}
