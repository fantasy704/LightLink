package top.lightlink.signal;

import top.lightlink.entity.po.UserInfo;

import java.util.List;

public class SignalMessage {
    private String type;
    private String serverId;
    private String fromUserId;
    private String toUserId;
    private String message;
    private Object data;
    private Long timestamp;
    private Integer onlineCount;
    private List<UserInfo> onlineUserIds;

    public List<UserInfo> getOnlineUserIds() {
        return onlineUserIds;
    }

    public void setOnlineUserIds(List<UserInfo> onlineUserIds) {
        this.onlineUserIds = onlineUserIds;
    }

    public Integer getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(Integer onlineCount) {
        this.onlineCount = onlineCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
