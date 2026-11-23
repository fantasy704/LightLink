package top.lightlink.signal;

public enum WSMessageType {
    PING("ping"),
    PONG("pong"),
    JOIN_ROOM("join_room"), // 加入房间 Client → Server
    LEAVE_ROOM("leave_room"), // 退出房间 Client → Server
    USER_JOINED("user_joined"), // 用户加入房间 	Server → Client
    USER_LEFT("user_left"), // 用户退出房间 	Server → Client
    TEXT_MESSAGE("text_message"), // 文本消息 	Server <-> Client
    OFFER("offer"), // SDP Client → Server → Client
    ANSWER("answer"), // SDP Answer Client → Server → Client
    ROOM_USERS("room_users"), // 房间用户列表 	Server → Client
    ICE_CANDIDATE("ice_candidate"), // ICE候选 	Server → Client
    MUTE_AUDIO("mute_audio"), // 静音音频 	Server → Client
    SPEAKING("speaking"), // 说话 	Server → Client
    RECONNECT("reconnect"), // 重新连接 	Server → Client
    ERROR("error"); // 错误 	Server → Client

    private String type;

    WSMessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static WSMessageType fromType(String type) {
        for (WSMessageType value : WSMessageType.values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }
}
