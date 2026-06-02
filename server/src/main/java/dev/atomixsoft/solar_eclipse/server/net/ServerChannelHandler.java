package dev.atomixsoft.solar_eclipse.server.net;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.LoginRequest;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.MoveRequest;
import dev.atomixsoft.solar_eclipse.server.logging.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerChannelHandler extends SimpleChannelInboundHandler<Packet> {

    private final Logger m_Logger;
    private final NetworkServer m_Network;

    public ServerChannelHandler(Logger logger, NetworkServer network) {
        super();
        m_Logger = logger;
        m_Network = network;
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
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet msg) {
        switch (msg) {
            case LoginRequest p -> {
                m_Logger.info("Login request: " + p.username());

                // ctx.writeAndFlush(new LoginResponsePacket(true, "Welcome " + p.username());
            }

            case MoveRequest p -> {
                m_Logger.info("Move request from entity " + p.entityId());

                /* TODO:
                    - Validate Movement
                    - Update World
                    - Broadcast to nearby players
                 */
            }

            default -> {
                m_Logger.warn("Unknown packet: " + msg.getClass().getSimpleName());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        m_Logger.error("Error: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

}
