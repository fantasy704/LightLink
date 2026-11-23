package top.lightlink.entity.query;

public class ServerMemberQuery {
    private String serverId;
    private String serverUser;
    private Boolean hasUserInfo;
    private Boolean hasServerInfo;

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerUser() {
        return serverUser;
    }

    public void setServerUser(String serverUser) {
        this.serverUser = serverUser;
    }

    public Boolean getHasUserInfo() {
        return hasUserInfo;
    }

    public void setHasUserInfo(Boolean hasUserInfo) {
        this.hasUserInfo = hasUserInfo;
    }

    public Boolean getHasServerInfo() {
        return hasServerInfo;
    }

    public void setHasServerInfo(Boolean hasServerInfo) {
        this.hasServerInfo = hasServerInfo;
    }
}
