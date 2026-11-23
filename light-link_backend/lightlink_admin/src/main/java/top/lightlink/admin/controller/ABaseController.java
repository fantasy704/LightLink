package top.lightlink.admin.controller;


import top.lightlink.entity.vo.ResponseVO;

public class ABaseController {
    protected <T> ResponseVO<T> getSuccessResponse(T data) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setCode(200);
        responseVO.setMsg("success");
        responseVO.setData(data);
        return responseVO;
    }
}
