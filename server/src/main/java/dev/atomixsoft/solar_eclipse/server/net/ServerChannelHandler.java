package dev.atomixsoft.solar_eclipse.server.net;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.LogoutRequest;
import dev.atomixsoft.solar_eclipse.server.logging.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerChannelHandler extends SimpleChannelInboundHandler<Packet> {

    private final Logger m_Logger;
    private final NetworkServer m_Network;
    private final PacketQueue m_PacketQueue;

    public ServerChannelHandler(Logger logger, NetworkServer network, PacketQueue packetQueue) {
        super();
        m_Logger = logger;
        m_Network = network;
        m_PacketQueue = packetQueue;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        m_Logger.info("Client connected: " + ctx.channel().remoteAddress());
        m_Network.getClients().add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        m_Logger.info("Client disconnected: " + ctx.channel().remoteAddress());
        m_Network.getClients().remove(ctx.channel());
        m_PacketQueue.enqueue(ctx.channel(), new LogoutRequest());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet msg) {
        m_PacketQueue.enqueue(ctx.channel(), msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        m_Logger.error("Error: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

}
