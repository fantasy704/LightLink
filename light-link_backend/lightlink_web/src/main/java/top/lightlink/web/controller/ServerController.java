package top.lightlink.web.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.lightlink.entity.dto.UserInfoDto;
import top.lightlink.entity.enums.ResponseCodeEnum;
import top.lightlink.entity.po.InviteLink;
import top.lightlink.entity.po.ServerMember;
import top.lightlink.entity.query.InviteLinkQuery;
import top.lightlink.entity.query.ServerMemberQuery;
import top.lightlink.entity.vo.ResponseVO;
import top.lightlink.exception.BusinessException;
import top.lightlink.mapper.InviteLinkMapper;
import top.lightlink.mapper.ServerMemberMapper;
import top.lightlink.service.ServerService;

import java.util.List;

@RestController
@RequestMapping("/server")
@Validated
public class ServerController extends ABaseController {

    @Autowired
    private ServerService serverService;
    @Autowired
    private InviteLinkMapper inviteLinkMapper;
    @Autowired
    private ServerMemberMapper serverMemberMapper;

    @RequestMapping("/loadServerList")
    public ResponseVO loadServerList(){
        return getSuccessResponse(serverService.loadServerList());
    }

    @RequestMapping("/createServer")
    public ResponseVO createServer(
            @NotEmpty String serverName,
            String serverInfo,
            @NotNull @Max(50) @Min(2) Integer maxNumber
    ){
        UserInfoDto userInfoDto = getUserInfoDto();
        if(userInfoDto == null){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        serverService.addServer(userInfoDto.getUid(), serverName, serverInfo, maxNumber);
        return getSuccessResponse(null);
    }

    @RequestMapping("/joinServerByLink")
    public ResponseVO joinServer(@NotEmpty String link){
        UserInfoDto userInfoDto = getUserInfoDto();
        if(userInfoDto == null){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        serverService.joinServer(userInfoDto.getUid(), link);
        return getSuccessResponse(null);
    }

    @RequestMapping("/loadSelfServerList")
    public ResponseVO loadSelfServerList(){
        UserInfoDto userInfoDto = getUserInfoDto();
        if(userInfoDto == null){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        return getSuccessResponse(serverService.loadSelfServerList(userInfoDto.getUid()));
    }

    @RequestMapping("/checkEnterServer")
    public ResponseVO checkEnterServer(@NotEmpty String serverId){
        UserInfoDto userInfoDto = getUserInfoDto();
        if(userInfoDto == null){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        return getSuccessResponse(serverService.checkServerAndServerMember(serverId, userInfoDto.getUid()));
    }

    @RequestMapping("/loadServerByLink")
    public ResponseVO loadServerByLink(@NotEmpty String link){
        return getSuccessResponse(serverService.loadServerByLink(link));
    }

    @RequestMapping("/loadInviteLink")
    public ResponseVO loadInviteLink(@NotEmpty String serverId){
        InviteLinkQuery query = new InviteLinkQuery();
        query.setLinkServer(serverId);
        InviteLink inviteLink = inviteLinkMapper.selectByParam(query);
        if(inviteLink == null){
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        return getSuccessResponse(inviteLink.getLinkKey());
    }

    @RequestMapping("/deleteServer")
    public ResponseVO deleteServer(@NotEmpty String serverId){
        UserInfoDto userInfoDto = getUserInfoDto();
        if(userInfoDto == null){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        serverService.deleteServer(serverId, userInfoDto.getUid());
        return getSuccessResponse(null);
    }

    @RequestMapping("/editServerNumber")
    public ResponseVO editServerNumber(@NotNull @Max(50) @Min(2) Integer maxNumber, @NotEmpty String serverId){
        UserInfoDto userInfoDto = getUserInfoDto();
        if(userInfoDto == null){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        serverService.editServerNumber(userInfoDto.getUid(), maxNumber, serverId);
        return getSuccessResponse(null);
    }

    @RequestMapping("/removeUserFromServer")
    public ResponseVO removeUserFromServer(@NotEmpty String serverId, @NotEmpty String memberId){
        UserInfoDto dto = getUserInfoDto();
        if(dto == null){
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        serverService.removeServerMember(dto.getUid(), serverId, memberId);
        return getSuccessResponse(null);
    }

    @RequestMapping("/loadServerMember")
    public ResponseVO loadServerMember(@NotEmpty String serverId){
        UserInfoDto dto = getUserInfoDto();
        if(dto == null){
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        ServerMemberQuery query = new ServerMemberQuery();
        query.setServerId(serverId);
        query.setHasUserInfo(true);
        List<ServerMember> serverMembers = serverMemberMapper.selectListByParam(query);
        return getSuccessResponse(serverMembers);
    }
}
