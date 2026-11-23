package top.lightlink.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.lightlink.entity.enums.ResponseCodeEnum;
import top.lightlink.entity.vo.ResponseVO;

@RestController
@RequestMapping("/web_test")
public class WebTestController extends ABaseController{
    @RequestMapping("/testHttp")
    public ResponseVO testHttp(){
        return new ResponseVO(ResponseCodeEnum.CODE_201);
    }
}
