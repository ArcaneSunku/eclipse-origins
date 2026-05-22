package dev.atomixsoft.solar_eclipse.client.net;

import dev.atomixsoft.solar_eclipse.client.logging.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

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
        // TODO:  Put actual game client logic here.
        //        For now, this is just an echo test.
        return "Client Received: " + message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        m_Logger.error("Error: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

}
