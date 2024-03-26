package own.yue.im.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import own.yue.im.protocol.ProtocolConstants;
import own.yue.im.util.RemoteAddressUtil;

import java.net.InetSocketAddress;

/**
 * 协议过滤
 */
@Slf4j
public class ProtocolFilter extends LengthFieldBasedFrameDecoder {
    public ProtocolFilter() {
        super(ProtocolConstants.MAX_FRAME_LENGTH, ProtocolConstants.LENGTH_FIELD_OFFSET, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        int magicNumber = in.getInt(in.readerIndex());
        if (magicNumber != ProtocolConstants.MAGIC_NUMBER_INT) {
            if (log.isDebugEnabled()) {
                log.debug("协议的 MAGIC_NUMBER 不匹配，期望：{} 实际：{} 将关闭连接：{}",
                        ProtocolConstants.MAGIC_NUMBER_INT, magicNumber, RemoteAddressUtil.getRemoteHostAddress(ctx))
                ;
            }
            ctx.channel().close();
            return null;
        }
        return super.decode(ctx, in);
    }
}
