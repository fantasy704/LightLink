package top.lightlink.web.websocket;

import com.alibaba.fastjson.JSONObject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import top.lightlink.common.Constants;
import top.lightlink.components.RedisComponent;
import top.lightlink.entity.dto.UserInfoDto;
import top.lightlink.entity.po.UserInfo;
import top.lightlink.entity.query.UserInfoQuery;
import top.lightlink.mapper.UserInfoMapper;
import top.lightlink.service.ServerService;
import top.lightlink.signal.SignalMessage;
import top.lightlink.signal.WSMessageType;
import top.lightlink.web.config.SpringContextUtil;
import top.lightlink.web.websocket.WebSocketConfigurator;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@ServerEndpoint(value = "/websocket/{server_id}", configurator = WebSocketConfigurator.class)
@Component
public class WebSocket {

    private final ServerService serviceService = SpringContextUtil.getBean(ServerService.class);
    private final RedisComponent redisComponent = SpringContextUtil.getBean(RedisComponent.class);
    private final UserInfoMapper userInfoMapper = SpringContextUtil.getBean(UserInfoMapper.class);

    private final Logger logger = Logger.getLogger(WebSocket.class.getName());
    private String serverId;
    private String uid;

    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, Session>> sessionPool = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "server_id") String serverId){
        this.serverId = serverId;
        logger.info("serverId：" + serverId);
        logger.info("有新的客户端连接，server_id：" + serverId);
        String token = (String) session.getUserProperties().get(Constants.TOKEN_USER);
        logger.info("token：" + token);
        if(token.equals(Constants.WS_TEST)){
            logger.info("测试连接");
            sendMessage(session, Constants.WS_TEST);
            closeSession(session);
            return;
        }
        UserInfoDto userInfoDto = redisComponent.getUserInfoDto(token);
        if(userInfoDto == null){
            closeSession(session);
            return;
        }
        logger.info("用户" + userInfoDto.getUid());
        Boolean isMember = serviceService.checkServerAndServerMember(serverId, userInfoDto.getUid());
        if (!isMember) {
            logger.info("用户" + userInfoDto.getUid() + "不是服务器成员，被踢出");
            closeSession(session);
            return;
        }
        logger.info("用户" + userInfoDto.getUid() + "是服务器成员，加入会话池");
        ConcurrentHashMap<String, Session> roomPool = sessionPool.computeIfAbsent(serverId, k -> new ConcurrentHashMap<>());

        this.uid = userInfoDto.getUid();
        SignalMessage msg = new SignalMessage();
        msg.setType(WSMessageType.USER_JOINED.getType());
        msg.setServerId(this.serverId);
        msg.setFromUserId(uid);
        msg.setToUserId("ALL");
        msg.setTimestamp(System.currentTimeMillis());
        msg.setMessage(uid + "加入房间");
        roomPool.put(uid, session);
        msg.setOnlineCount(roomPool.size());

        List<String> ids = roomPool.keySet().stream().toList();
        UserInfoQuery userInfoQuery = new UserInfoQuery();
        userInfoQuery.setUidList(ids);
        List<UserInfo> userInfoList = userInfoMapper.selectListByParam(userInfoQuery);
        msg.setOnlineUserIds(userInfoList);
        logger.info("list:" + userInfoList.stream().map(JSONObject::toJSONString).toList());
        logger.info("json:" + JSONObject.toJSONString(userInfoList.stream().map(JSONObject::toJSONString).toList()));

        sessionPool.put(serverId, roomPool);
        broadcastToRoom(roomPool, msg);
    }

    @OnMessage
    public void onMessage(String message){
        ConcurrentHashMap<String, Session> roomPool = sessionPool.get(serverId);
        if(roomPool == null){
            return;
        }
        logger.info("收到消息：" + message);
        SignalMessage msg = JSONObject.parseObject(message, SignalMessage.class);
        WSMessageType msgType = WSMessageType.fromType(msg.getType());
        if(msgType == null){
            return;
        }
        switch(msgType){
            case PING:
                pong(roomPool, msg);
                break;
            case TEXT_MESSAGE:
            case MUTE_AUDIO:
            case SPEAKING:
                broadcastToRoom(roomPool, msg);
                break;
            case OFFER:
            case ANSWER:
            case ICE_CANDIDATE:
                broadcastExceptSender(roomPool, msg);
                break;
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason){
        ConcurrentHashMap<String, Session> roomPool = sessionPool.get(serverId);
        if(roomPool == null){
            return;
        }
        if(this.uid == null){
            return;
        }
        roomPool.remove(uid);
        logger.info("用户" + uid + "关闭连接");
        if(roomPool.isEmpty()){
            sessionPool.remove(serverId);
            logger.info("房间" + serverId + "已空，移除会话池");
            return;
        }
        SignalMessage msg = new SignalMessage();
        msg.setType(WSMessageType.USER_LEFT.getType());
        msg.setServerId(this.serverId);
        msg.setFromUserId(uid);
        msg.setToUserId("ALL");
        msg.setTimestamp(System.currentTimeMillis());
        msg.setMessage(uid + "退出房间");
        msg.setOnlineCount(roomPool.size());
        List<String> ids = roomPool.keySet().stream().toList();
        UserInfoQuery userInfoQuery = new UserInfoQuery();
        userInfoQuery.setUidList(ids);
        List<UserInfo> userInfoList = userInfoMapper.selectListByParam(userInfoQuery);
        msg.setOnlineUserIds(userInfoList);
        broadcastToRoom(roomPool, msg);
    }

    private void closeSession(Session session){
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "404"));
        } catch (IOException e) {
            logger.severe("关闭连接失败：" + e.getMessage());
        }
    }

    private void pong(ConcurrentHashMap<String, Session> roomPool, SignalMessage msg){
        msg.setType(WSMessageType.PONG.getType());
        msg.setMessage("pong");
        Session session = roomPool.get(msg.getToUserId());
        sendMessage(session, JSONObject.toJSONString(msg));
    }

    private void sendMessage(Session session, String message){
        if(session == null){
            return;
        }
        if(!session.isOpen()){
            return;
        }
        synchronized (session){
            logger.info("下发消息：" + message);
            try{
                session.getBasicRemote().sendText(message);
            }catch(IOException e){
                logger.severe(e.getMessage());
            }
        }
    }

    private void broadcastExceptSender(ConcurrentHashMap<String, Session> roomPool, SignalMessage msg){
        String json = JSONObject.toJSONString(msg);
        for (Map.Entry<String, Session> entry : roomPool.entrySet()) {
            if (!entry.getKey().equals(msg.getFromUserId())) {
                sendMessage(entry.getValue(), json);
            }
        }
    }

    private void broadcastToRoom(ConcurrentHashMap<String, Session> roomPool, SignalMessage msg){
        String json = JSONObject.toJSONString(msg);
        for (Map.Entry<String, Session> entry : roomPool.entrySet()) {
            sendMessage(entry.getValue(), json);
        }
    }
}