package top.lightlink.entity.po;

public class Server {
    private String serverId;
    private String serverName;
    private String serverMaster;
    private String serverInfo;
    private Integer maxNumber;
    private String serverImage;
    private String extra;

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
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

    public String getServerMaster() {
        return serverMaster;
    }

    public Integer getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(Integer maxNumber) {
        this.maxNumber = maxNumber;
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
}
