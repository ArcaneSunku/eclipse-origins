package dev.atomixsoft.solar_eclipse.server;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;
import dev.atomixsoft.solar_eclipse.core.net.packet.impl.EntityMovePacket;
import dev.atomixsoft.solar_eclipse.core.net.packet.impl.LoginPacket;
import dev.atomixsoft.solar_eclipse.server.logging.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerChannelHandler extends SimpleChannelInboundHandler<Packet> {

    private final Logger m_Logger;

    public ServerChannelHandler(Logger logger) {
        super();
        m_Logger = logger;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        m_Logger.info("Client connected: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        m_Logger.info("Client disconnected: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet msg) {
        switch (msg) {
            case LoginPacket p -> {
                m_Logger.info("Login request: " + p.username());

                // ctx.writeAndFlush(new LoginResponsePacket(true, "Welcome " + p.username());
            }

            case EntityMovePacket p -> {
                m_Logger.info("Move request from entity " + p.id());

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
