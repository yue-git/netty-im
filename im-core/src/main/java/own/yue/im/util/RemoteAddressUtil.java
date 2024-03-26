package own.yue.im.util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class RemoteAddressUtil {
    private RemoteAddressUtil() {
    }

    public static String getRemoteHostAddress(ChannelHandlerContext ctx) {
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
    }

    public static String getRemoteHostAddress(SocketChannel socketChannel) {
        return socketChannel.remoteAddress().getAddress().getHostAddress();
    }
}
