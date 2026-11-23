package top.lightlink.entity.enums;

public enum PassedStatus {
    REJECT(0, false, "拒绝"),
    PASS(1,true, "通过");

    private final Integer code;
    private final Boolean status;
    private final String description;

    PassedStatus(Integer code,Boolean pass, String description){
        this.code = code;
        this.status = pass;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getStatus() {
        return status;
    }

    public Integer getCode() {
        return code;
    }

    public static PassedStatus getByCode(Integer code){
        for(PassedStatus status : PassedStatus.values()){
            if(status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
