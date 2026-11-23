package top.lightlink.entity.vo;

import top.lightlink.entity.enums.ResponseCodeEnum;

public class ResponseVO<T> {
    private Integer code;
    private String msg;
    private T data;

    public ResponseVO() {
    }

    public ResponseVO(ResponseCodeEnum e){
        this.code = e.getCode();
        this.msg = e.getMsg();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
