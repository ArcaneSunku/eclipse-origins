package dev.atomixsoft.solar_eclipse.server.net;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PacketQueue {

    private final Queue<Packet> m_Queue;

    public PacketQueue() {
        m_Queue = new ConcurrentLinkedQueue<>();
    }

    public void push(Packet packet) {
        m_Queue.add(packet);
    }

    public Packet poll() {
        return m_Queue.poll();
    }

    public boolean isEmpty() {
        return m_Queue.isEmpty();
    }

}
