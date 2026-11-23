package top.lightlink.entity.query;

public class ServerQuery {
    private String serverId;
    private String serverName;
    private String serverMaster;
    private Integer maxNumber;

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

    public String getServerMaster() {
        return serverMaster;
    }

    public void setServerMaster(String serverMaster) {
        this.serverMaster = serverMaster;
    }

    public Integer getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(Integer maxNumber) {
        this.maxNumber = maxNumber;
    }
}
