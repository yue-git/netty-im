package own.yue.im.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import own.yue.im.annotation.ImService;
import own.yue.im.annotation.ImServiceHandler;
import own.yue.im.protocol.ProtocolConstants;
import own.yue.im.protocol.ReqContext;
import own.yue.im.protocol.payload.dto.client2server.LoginReq;
import own.yue.im.protocol.payload.dto.server2client.LoginRes;
import own.yue.im.util.session.Session;

@Slf4j
@ImService
@Component
public class LoginService {
    @ImServiceHandler(inboundMsgType = ProtocolConstants.MESSAGE_TYPE.LOGIN_REQ, outboundMsgType = ProtocolConstants.MESSAGE_TYPE.LOGIN_RES)
    public LoginRes login(ReqContext reqContext, LoginReq loginReq) {
        log.info("调用login");
        Session session = new Session();
        session.setUserId(loginReq.getLogin());
        session.setLogin(true);
        reqContext.bindSession(session);
        return new LoginRes(true);
    }
}
