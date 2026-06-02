package dev.atomixsoft.solar_eclipse.server;


import dev.atomixsoft.solar_eclipse.core.event.EventBus;
import dev.atomixsoft.solar_eclipse.core.net.codec.PacketDecoder;
import dev.atomixsoft.solar_eclipse.core.net.codec.PacketEncoder;
import dev.atomixsoft.solar_eclipse.core.net.PacketRegistry;
import dev.atomixsoft.solar_eclipse.server.config.Configuration;
import dev.atomixsoft.solar_eclipse.server.console.ConsoleThread;
import dev.atomixsoft.solar_eclipse.server.logging.Logger;
import dev.atomixsoft.solar_eclipse.server.net.NetworkServer;
import dev.atomixsoft.solar_eclipse.server.net.ServerChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.concurrent.TimeUnit;

public class Server {

    private static Server m_Instance = null;

    public static EventBus event_bus() {
        return m_Instance.m_EventBus;
    }

    public static NetworkServer network() {
        return m_Instance.m_Network;
    }

    private final int m_Port;

    private final EventLoopGroup m_IncomingConnections;
    private final EventLoopGroup m_ClientWorkers;

    private final Configuration m_ConfigInfo;
    private final Logger m_Logger;

    private Thread m_Console;
    private EventBus m_EventBus;
    private Channel m_ServerChannel;

    private NetworkServer m_Network;

    public Server() {
        m_ConfigInfo = new Configuration(Configuration.SupportedConfigFileTypes.INI, "server/server.ini");
        m_Logger = new Logger(Server.class.getSimpleName(), Logger.SupportedLogHandlerTypes.ASYNC_CONSOLE,
                m_ConfigInfo.getLogLevel(), m_ConfigInfo.getLogPattern());

        m_Port = m_ConfigInfo.getPort();
        m_IncomingConnections = new NioEventLoopGroup();
        m_ClientWorkers = new NioEventLoopGroup();

        if(m_Instance == null)
            m_Instance = this;
    }

    private void initialize() {
        PacketRegistry.Initialize();
        m_EventBus = new EventBus();
        m_Network = new NetworkServer();

        m_Console = new Thread(new ConsoleThread(m_EventBus, this::shutdown), "Console_Thread");
        m_Console.setDaemon(true);
        m_Console.start();
    }

    public void run() throws Exception {
        initialize();

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
                                                                   m_ConfigInfo.getLogPattern()), m_Network));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.bind(m_Port).sync();
            m_ServerChannel = future.channel();
            m_Logger.info(m_ConfigInfo.getName() + " server instance accepting RPC on port " + m_Port + ".");

            future.channel().closeFuture().sync();
        } finally {
            shutdown();
        }
    }

    private void shutdown() {
        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if(m_ServerChannel != null)
                m_ServerChannel.close();

            m_Console.join(1L);
        } catch (Exception e) {
            m_Logger.error(e.getMessage());
        } finally {
            m_EventBus.shutdown();

            m_IncomingConnections.shutdownGracefully(0, 0, TimeUnit.NANOSECONDS);
            m_ClientWorkers.shutdownGracefully(0, 0, TimeUnit.NANOSECONDS);
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
