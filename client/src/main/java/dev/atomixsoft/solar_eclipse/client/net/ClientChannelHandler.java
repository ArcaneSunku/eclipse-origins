package dev.atomixsoft.solar_eclipse.client.net;

import dev.atomixsoft.solar_eclipse.client.logging.Logger;
import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientChannelHandler extends SimpleChannelInboundHandler<Packet> {

    private final Logger m_Logger;
    private final NetworkClient m_Network;

    public ClientChannelHandler(Logger logger, NetworkClient network) {
        super();

        m_Logger = logger;
        m_Network = network;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        m_Logger.info("Connected to server!");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        m_Logger.info("Disconnected from server!");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        m_Network.queue(packet);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        m_Logger.error("Error: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

}
