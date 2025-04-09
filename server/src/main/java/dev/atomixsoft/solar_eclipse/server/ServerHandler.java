package dev.atomixsoft.solar_eclipse.server;

import dev.atomixsoft.solar_eclipse.server.logging.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger m_Logger;

    public ServerHandler(Logger logger) {
        m_Logger = logger;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        m_Logger.info("Client connected: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        String data = in.toString(CharsetUtil.UTF_8);
        m_Logger.info("Received: " + data.trim());

        // Echo for now
        ctx.writeAndFlush(in.retain());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        m_Logger.warn("Client disconnected: " + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        m_Logger.error("Error: " + cause.getMessage());
        ctx.close();
    }

}
