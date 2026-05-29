package dev.atomixsoft.solar_eclipse.client.events;

import dev.atomixsoft.solar_eclipse.client.net.NetworkClient;
import dev.atomixsoft.solar_eclipse.core.event.interfaces.EventConsumer;
import dev.atomixsoft.solar_eclipse.core.event.types.SendPacketEvent;

public class PacketListener implements EventConsumer<SendPacketEvent> {

    private final NetworkClient m_Network;

    public PacketListener(NetworkClient network) {
        m_Network = network;
    }

    @Override
    public void accept(SendPacketEvent event) {
        if(event.handled) return;

        if(m_Network != null && m_Network.isConnected()) {
            m_Network.send(event.getPacket());
            event.handled = true;
        }
    }

}
