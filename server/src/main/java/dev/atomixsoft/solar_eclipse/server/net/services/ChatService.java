package dev.atomixsoft.solar_eclipse.server.net.services;

import dev.atomixsoft.solar_eclipse.core.net.packet.notification.ChatMessageBroadcast;
import dev.atomixsoft.solar_eclipse.server.net.NetworkServer;

public class ChatService {

    private final NetworkServer m_Network;

    public ChatService(NetworkServer network) {
        m_Network = network;
    }

    public void broadcast(String sender, String message) {
        m_Network.broadcast(new ChatMessageBroadcast(sender, message, (int) System.currentTimeMillis()));
    }

}
