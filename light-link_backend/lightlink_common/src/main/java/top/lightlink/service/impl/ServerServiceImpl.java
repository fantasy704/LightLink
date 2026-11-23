package top.lightlink.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.lightlink.entity.enums.ResponseCodeEnum;
import top.lightlink.entity.po.InviteLink;
import top.lightlink.entity.po.Server;
import top.lightlink.entity.po.ServerMember;
import top.lightlink.entity.query.InviteLinkQuery;
import top.lightlink.entity.query.ServerMemberQuery;
import top.lightlink.entity.query.ServerQuery;
import top.lightlink.exception.BusinessException;
import top.lightlink.mapper.InviteLinkMapper;
import top.lightlink.mapper.ServerMapper;
import top.lightlink.mapper.ServerMemberMapper;
import top.lightlink.service.ServerService;
import top.lightlink.utils.StringUtil;

import java.util.List;
import java.util.Random;

@Service
public class ServerServiceImpl implements ServerService {

    @Autowired
    private ServerMapper serverMapper;
    @Autowired
    private ServerMemberMapper serverMemberMapper;
    @Autowired
    private InviteLinkMapper inviteLinkMapper;
    @Override
    public List<Server> loadServerList() {
        return serverMapper.selectListByParam(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addServer(String serverMaster,String serverName, String serverInfo, Integer maxNumber) {
        ServerQuery query = new ServerQuery();
        query.setServerName(serverName);
        List<Server> servers = serverMapper.selectListByParam(query);
        if(servers == null || !servers.isEmpty()){
            throw new BusinessException("服务器名称已存在");
        }
        query.setServerMaster(serverMaster);
        query.setServerName(null);
        int count = serverMapper.selectCountByParam(query);
        if(count >= 5){
            throw new BusinessException("单个用户最多只能创建5个服务器");
        }

        Server server = new Server();
        String serverId = "S" + (new Random().nextInt(89999999) + 10000000);
        server.setServerId(serverId);
        server.setServerMaster(serverMaster);
        server.setServerName(serverName);
        server.setServerInfo(serverInfo);
        server.setMaxNumber(maxNumber);
        serverMapper.insert(server);

        ServerMember serverMember = new ServerMember();
        serverMember.setServerId(serverId);
        serverMember.setServerUser(serverMaster);
        serverMemberMapper.insert(serverMember);

        String linkKey = StringUtil.randomString(20);
        InviteLink inviteLink = new InviteLink();
        inviteLink.setLinkKey(linkKey);
        inviteLink.setLinkServer(serverId);
        inviteLinkMapper.insert(inviteLink);
    }

    @Override
    public void joinServer(String uid, String linkKey) {
        InviteLinkQuery query = new InviteLinkQuery();
        query.setLinkKey(linkKey);
        InviteLink inviteLink = inviteLinkMapper.selectByParam(query);
        if(inviteLink == null){
            throw new BusinessException("邀请链接不存在");
        }
        ServerMemberQuery serverMemberQuery = new ServerMemberQuery();
        serverMemberQuery.setServerId(inviteLink.getLinkServer());
        serverMemberQuery.setServerUser(uid);
        ServerMember serverMember = serverMemberMapper.selectByParam(serverMemberQuery);
        if(serverMember != null){
            throw new BusinessException(200, "您已加入服务器");
        }
        ServerMemberQuery countQuery = new ServerMemberQuery();
        countQuery.setServerId(inviteLink.getLinkServer());
        int count = serverMemberMapper.selectCountByParam(countQuery);
        ServerQuery serverQuery = new ServerQuery();
        serverQuery.setServerId(inviteLink.getLinkServer());
        Server server = serverMapper.selectByParam(serverQuery);
        if(count >= server.getMaxNumber()){
            throw new BusinessException(400, "服务器已满");
        }
        serverMember = new ServerMember();
        serverMember.setServerId(inviteLink.getLinkServer());
        serverMember.setServerUser(uid);
        serverMemberMapper.insert(serverMember);
    }

    @Override
    public List<ServerMember> loadSelfServerList(String uid) {
        ServerMemberQuery query = new ServerMemberQuery();
        query.setServerUser(uid);
        query.setHasServerInfo(true);
        return serverMemberMapper.selectListByParam(query);
    }

    @Override
    public Boolean checkServerAndServerMember(String serverId, String uid) {
        ServerMemberQuery serverMemberQuery = new ServerMemberQuery();
        serverMemberQuery.setServerId(serverId);
        serverMemberQuery.setServerUser(uid);
        return serverMemberMapper.selectByParam(serverMemberQuery) != null;
    }

    @Override
    public Server loadServerByLink(String ink) {
        InviteLinkQuery query = new InviteLinkQuery();
        query.setLinkKey(ink);
        InviteLink inviteLink = inviteLinkMapper.selectByParam(query);
        ServerQuery serverQuery = new ServerQuery();
        if(inviteLink == null){
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        serverQuery.setServerId(inviteLink.getLinkServer());
        return serverMapper.selectByParam(serverQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteServer(String serverId, String uid) {
        ServerQuery serverQuery = new ServerQuery();
        serverQuery.setServerMaster(uid);
        serverQuery.setServerId(serverId);
        Server server = serverMapper.selectByParam(serverQuery);
        if(server == null){
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        if(!server.getServerMaster().equals(uid)){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        serverMapper.deleteByParam(serverQuery);
        InviteLinkQuery inviteLinkQuery = new InviteLinkQuery();
        inviteLinkQuery.setLinkServer(serverId);
        inviteLinkMapper.deleteByParam(inviteLinkQuery);
        ServerMemberQuery serverMemberQuery = new ServerMemberQuery();
        serverMemberQuery.setServerId(serverId);
        serverMemberMapper.deleteByParam(serverMemberQuery);
    }

    @Override
    public void editServerNumber(String uid, Integer maxNumber, String serverId) {
        System.out.println(uid);
        System.out.println(maxNumber);
        System.out.println(serverId);
        ServerQuery serverQuery = new ServerQuery();
        serverQuery.setServerId(serverId);
        Server server = serverMapper.selectByParam(serverQuery);
        if(server == null){
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        if(!server.getServerMaster().equals(uid)){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        server.setMaxNumber(maxNumber);
        serverMapper.updateMaxNumber(server);
    }

    @Override
    public void removeServerMember(String uid, String serverId, String memberId) {
        ServerQuery serverQuery = new ServerQuery();
        serverQuery.setServerId(serverId);
        Server server = serverMapper.selectByParam(serverQuery);
        if(!server.getServerMaster().equals(uid)){ //不是我的服务器
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if(server.getServerMaster().equals(memberId)){ //移除自己
            throw new BusinessException("无法移除创建者，可删除服务器");
        }
        ServerMemberQuery serverMemberQuery = new ServerMemberQuery();
        serverMemberQuery.setServerId(serverId);
        serverMemberQuery.setServerUser(memberId);
        serverMemberMapper.deleteByParam(serverMemberQuery);
    }


}
