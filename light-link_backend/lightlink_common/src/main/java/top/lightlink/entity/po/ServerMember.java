package top.lightlink.entity.po;

public class ServerMember {
    private String serverId;
    private String serverUser;

    private String serverName;
    private String serverMaster;
    private String serverInfo;
    private Integer maxNumber;
    private String serverImage;
    private String extra;

    private String memberName;

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

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

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerMaster() {
        return serverMaster;
    }

    public void setServerMaster(String serverMaster) {
        this.serverMaster = serverMaster;
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }

    public Integer getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(Integer maxNumber) {
        this.maxNumber = maxNumber;
    }

    public String getServerImage() {
        return serverImage;
    }

    public void setServerImage(String serverImage) {
        this.serverImage = serverImage;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
