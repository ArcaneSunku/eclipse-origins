package dev.atomixsoft.solar_eclipse.server;

import dev.atomixsoft.solar_eclipse.server.logging.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class ConnectionHandler extends ChannelInboundHandlerAdapter {

    private final Logger m_Logger;

    public ConnectionHandler(Logger logger) {
        super();
        m_Logger = logger;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = (String) msg;
        m_Logger.info("Received: " + msg);

        String response = processMessage(message);
        ctx.writeAndFlush(response);
        m_Logger.info("Sent: " + response);
    }

    private String processMessage(String message) {
        // TODO:  But actual game server logic here.
        //        For now, this is just an echo test.
        return "Server Received: " + message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        m_Logger.error("Error: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

}
