package dev.atomixsoft.solar_eclipse.server.net;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;
import io.netty.channel.Channel;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PacketQueue {

    private final Queue<QueuedPacket> m_Queue;

    public PacketQueue() {
        m_Queue = new ConcurrentLinkedQueue<>();
    }

    public void enqueue(Channel channel, Packet packet) {
        m_Queue.add(new QueuedPacket(channel, packet));
    }

    public QueuedPacket poll() {
        return m_Queue.poll();
    }

    public boolean isEmpty() {
        return m_Queue.isEmpty();
    }

}
