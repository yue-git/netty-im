package own.yue.im.protocol;

import io.netty.channel.Channel;
import lombok.Getter;
import own.yue.im.util.session.Session;
import own.yue.im.util.session.SessionUtil;

/**
 * 请求上下文
 */
public class ReqContext {
    private Session session;
    private Channel channel;

    public boolean hasLogin(){
        return session.isLogin();
    }
    public String getUserId(){
        return session.getUserId();
    }

    public boolean bindSession(Session session){
        boolean b = SessionUtil.bindSession(session, channel);
        if(b){
            this.session = session;
        }
        return b;
    }

    public ReqContext(Session session, Channel channel) {
        this.session = session;
        this.channel = channel;
    }
}
