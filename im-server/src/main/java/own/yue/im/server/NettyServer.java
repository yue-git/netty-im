package own.yue.im.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import own.yue.im.handler.ExceptionHandler;
import own.yue.im.handler.ProtocolCodecHandler;
import own.yue.im.handler.ProtocolFilter;
import own.yue.im.server.handler.ServerCommonHandler;
import own.yue.im.util.RemoteAddressUtil;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class NettyServer implements AutoCloseable {
    private final ServerBootstrap serverBootstrap;
    private final NioEventLoopGroup boosGroup;
    private final NioEventLoopGroup workGroup;

    @Resource
    private ProtocolCodecHandler protocolCodecHandler;
    @Resource
    private ServerCommonHandler serverCommonHandler;
    @Resource
    private ExceptionHandler exceptionHandler;

    public NettyServer() {
        boosGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boosGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        if (log.isDebugEnabled()) {
//                            从网络层面来看，netty层之前如果有LB之类，会导致这个ip有误
                            log.debug("a new channel init: {}", RemoteAddressUtil.getRemoteHostAddress(nioSocketChannel));
                        }
                        ChannelPipeline p = nioSocketChannel.pipeline();
//                        可以添加证书双向认证，进一步保证安全
//                        p.addLast(new SslHandler());
                        p.addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
                        p.addLast(new ProtocolFilter());
                        p.addLast(protocolCodecHandler);
                        p.addLast(serverCommonHandler);
                        p.addLast(exceptionHandler);
                    }
                });

        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        serverBootstrap.attr(AttributeKey.newInstance("instanceName"), "im-server");
    }

    @Override
    public void close() throws Exception {
        workGroup.shutdownGracefully();
        boosGroup.shutdownGracefully();
    }

    public void start() throws InterruptedException {
        serverBootstrap.bind(3456).sync();
    }
}
