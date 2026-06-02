package dev.atomixsoft.solar_eclipse.server;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.ChatMessageRequest;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.MoveIntent;
import dev.atomixsoft.solar_eclipse.server.net.PacketQueue;

public class ServerThread implements Runnable {

    private final PacketQueue m_PacketQueue;

    private volatile boolean m_Running;

    public ServerThread(PacketQueue packetQueue) {
        m_PacketQueue = packetQueue;
        m_Running = true;
    }

    @Override
    public void run() {
        double accumulator = 0.0;
        double optimal = 1.0 / 20.0;
        double currentTime = System.nanoTime() / 1e9;
        double newTime, frameTime;

        while(m_Running) {
            newTime = System.nanoTime() / 1e9;
            frameTime = newTime - currentTime;
            currentTime = newTime;
            accumulator += frameTime;

            while(accumulator >= optimal) {
                processPackets();
                // Update Game World
                sendSnapshots();
                accumulator -= optimal;
            }

            sleep(currentTime);
        }
    }

    private void processPackets() {
        Packet packet;

        while((packet = m_PacketQueue.poll()) != null) {
            if(packet instanceof MoveIntent req) {
                // TODO Implement MoveIntent Response
            }

            if(packet instanceof ChatMessageRequest req) {
                // TODO: Implement ChatMessage Broadcast Response
            }
        }
    }

    private void sendSnapshots() {
        // TODO: Handle broadcasting world updates to connected clients
    }

    private void sleep(double currentTime) {
        double desiredTime = 1.0 / 20.0;
        long sleepTime = (long) ((currentTime - System.nanoTime() + desiredTime) / 1e9);

        try {
            if (sleepTime > 0)
                Thread.sleep(sleepTime);

        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

}
