package dev.atomixsoft.solar_eclipse.server;


import dev.atomixsoft.solar_eclipse.core.net.codec.PacketDecoder;
import dev.atomixsoft.solar_eclipse.core.net.codec.PacketEncoder;
import dev.atomixsoft.solar_eclipse.server.config.Configuration;
import dev.atomixsoft.solar_eclipse.server.logging.Logger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Server {

    private final int m_Port;

    private final EventLoopGroup m_IncomingConnections;
    private final EventLoopGroup m_ClientWorkers;

    private final Configuration m_ConfigInfo;
    private final Logger m_Logger;

    public Server() {
        m_ConfigInfo = new Configuration(Configuration.SupportedConfigFileTypes.INI, "server/server.ini");
        m_Logger = new Logger(Server.class.getSimpleName(), Logger.SupportedLogHandlerTypes.ASYNC_CONSOLE,
                m_ConfigInfo.getLogLevel(), m_ConfigInfo.getLogPattern());

        m_Port = m_ConfigInfo.getPort();
        m_IncomingConnections = new NioEventLoopGroup();
        m_ClientWorkers = new NioEventLoopGroup();
    }

    public void run() throws Exception {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(m_IncomingConnections, m_ClientWorkers)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, 0, 4));

                            pipeline.addLast(new PacketDecoder());
                            pipeline.addLast(new LengthFieldPrepender(4));
                            pipeline.addLast(new PacketEncoder());

                            pipeline.addLast(new ServerChannelHandler(new Logger(Server.class.getSimpleName(),
                                                                   Logger.SupportedLogHandlerTypes.ASYNC_CONSOLE,
                                                                   m_ConfigInfo.getLogLevel(),
                                                                   m_ConfigInfo.getLogPattern())));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.bind(m_Port).sync();
            m_Logger.info(m_ConfigInfo.getName() + " server instance accepting RPC on port " + m_Port + ".");

            future.channel().closeFuture().sync();
        } finally {
            m_IncomingConnections.shutdownGracefully();
            m_ClientWorkers.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            new Server().run();
        } catch (Exception e) {
            throw new RuntimeException("Server failed to start: " + e.getMessage());
        }
    }
}
