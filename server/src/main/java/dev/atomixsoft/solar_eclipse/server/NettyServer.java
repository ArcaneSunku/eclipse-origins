package dev.atomixsoft.solar_eclipse.server;

import dev.atomixsoft.solar_eclipse.server.logging.Logger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    private final Logger m_Logger;
    private final EventLoopGroup m_Boss, m_Worker;

    private Channel m_Server;

    public NettyServer(Logger logger) {
        m_Logger = logger;

        m_Boss = new NioEventLoopGroup(1);
        m_Worker = new NioEventLoopGroup();
    }

    public synchronized void start(int port) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(m_Boss, m_Worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ServerHandler(m_Logger));
                    }
                });

        ChannelFuture future = bootstrap.bind(port).sync();
        m_Server = future.channel();
        m_Logger.info("Server listening on port: " + port);
    }

    public synchronized void shutdown() {
        m_Boss.shutdownGracefully();
        m_Worker.shutdownGracefully();
        m_Logger.info("Server shutting down...");
    }

}
