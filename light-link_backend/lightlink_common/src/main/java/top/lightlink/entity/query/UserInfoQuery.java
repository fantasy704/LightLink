package top.lightlink.entity.query;

import java.util.List;

public class UserInfoQuery{
    private String uid;
    private String email;
    private String name;
    private String emailOrName;
    private List<String> uidList;

    public List<String> getUidList() {
        return uidList;
    }

    public void setUidList(List<String> uidList) {
        this.uidList = uidList;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailOrName() {
        return emailOrName;
    }

    public void setEmailOrName(String emailOrName) {
        this.emailOrName = emailOrName;
    }
}
