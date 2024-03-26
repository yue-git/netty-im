package own.yue.im.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import own.yue.im.handler.ProtocolCodecHandler;
import own.yue.im.handler.ProtocolFilter;
import own.yue.im.protocol.Packet;
import own.yue.im.protocol.ProtocolConstants;
import own.yue.im.protocol.payload.dto.client2server.LoginReq;
import own.yue.im.protocol.payload.dto.client2server.MessageSendToUser;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class NettyClient implements AutoCloseable {
    @Resource
    private ProtocolCodecHandler protocolCodecHandler;
    private ChannelFuture channelFuture;
    private NioEventLoopGroup workGroup;
    private Bootstrap bootstrap;

    @Resource
    private ClientCommonHandler clientCommonHandler;



    public NettyClient() {
        workGroup = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline p = nioSocketChannel.pipeline();
                        p.addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
                        p.addLast(new ProtocolFilter());
                        p.addLast(protocolCodecHandler);
                        p.addLast(clientCommonHandler);
                    }
                });
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    }

    public void connect() throws InterruptedException {
        channelFuture = bootstrap.connect("127.0.0.1", 3456)
                .addListener(future -> {
                    if (future.isSuccess()) {
                        log.info("连接服务器成功");
                    } else {
                        log.info("连接服务器失败");
                        bootstrap.config().group().schedule(() -> {
                            try {
                                connect();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }, 5, TimeUnit.SECONDS);
                    }
                });


    }

    public void sendMessage(String s) {
        String[] split = s.split(" ");
        short i = Short.parseShort(split[0]);
        Packet outBuoudPacket;
        switch (i) {
            case ProtocolConstants.MESSAGE_TYPE.LOGIN_REQ: {
                outBuoudPacket = Packet.build()
                        .version(ProtocolConstants.PROTOCOL_VERSION.VERSION_1)
                        .msgId(1)
                        .msgType(ProtocolConstants.MESSAGE_TYPE.LOGIN_REQ)
                        .serial(ProtocolConstants.Serial.KRYO)
                        .compress(ProtocolConstants.Compress.NONE.getBitValue())
                        .payload(new LoginReq(split[1])).build();
                break;
            }
            case ProtocolConstants.MESSAGE_TYPE.MESSAGE_SEND_TO_USER: {
                outBuoudPacket = Packet.build()
                        .version(ProtocolConstants.PROTOCOL_VERSION.VERSION_1)
                        .msgId(1)
                        .msgType(ProtocolConstants.MESSAGE_TYPE.MESSAGE_SEND_TO_USER)
                        .serial(ProtocolConstants.Serial.KRYO)
                        .compress(ProtocolConstants.Compress.NONE.getBitValue())
                        .payload(new MessageSendToUser(split[1], split[2])).build();
                break;
            }
            default: {
                log.info("未适配的消息类型：{}", i);
                return;
            }

        }

        channelFuture.channel().writeAndFlush(outBuoudPacket);
    }

    @Override
    public void close() throws Exception {
        workGroup.shutdownGracefully();
    }
}
