package dev.atomixsoft.solar_eclipse.client.net;

import dev.atomixsoft.solar_eclipse.client.logging.Logger;
import dev.atomixsoft.solar_eclipse.core.net.codec.PacketDecoder;
import dev.atomixsoft.solar_eclipse.core.net.codec.PacketEncoder;
import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkClient {

    private final Queue<Packet> m_Incoming;

    private EventLoopGroup m_Group;
    private Channel m_Channel;

    public NetworkClient() {
        m_Incoming = new ConcurrentLinkedQueue<>();
    }

    public void connect(String host, int port, Logger logger) throws Exception {
        m_Group = new NioEventLoopGroup();
        NetworkClient instance = this;

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(m_Group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, 0, 4));
                        pipeline.addLast(new LengthFieldPrepender(4));

                        pipeline.addLast(new PacketDecoder());
                        pipeline.addLast(new PacketEncoder());

                        pipeline.addLast(new ClientChannelHandler(logger, instance));
                    }

                });

        ChannelFuture future = bootstrap.connect(host, port).sync();
        m_Channel = future.channel();
    }

    public void send(Packet packet) {
        if(m_Channel == null || !m_Channel.isActive())
            return;

        m_Channel.writeAndFlush(packet);
    }

    public void queue(Packet packet) {
        m_Incoming.add(packet);
    }

    public void disconnect() {
        try {
            if(m_Channel != null)
                m_Channel.close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(m_Group != null)
            m_Group.shutdownGracefully();
    }

    public Packet poll() {
        return m_Incoming.poll();
    }

    public boolean connected() {
        return m_Channel != null && m_Channel.isActive();
    }

}
