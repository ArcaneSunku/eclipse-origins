package dev.atomixsoft.solar_eclipse.server.net;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class NetworkServer {

    private final ChannelGroup m_Clients;

    public NetworkServer() {
        m_Clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public void add(Channel channel) {
        m_Clients.add(channel);
    }

    public void remove(Channel channel) {
        m_Clients.remove(channel);
    }

    public void send(Channel channel, Packet packet) {
        if(channel == null || !channel.isActive())
            return;

        channel.writeAndFlush(packet);
    }

    public ChannelGroupFuture broadcast(Packet packet) {
        return m_Clients.writeAndFlush(packet);
    }

    public ChannelGroup getClients() {
        return m_Clients;
    }

}
