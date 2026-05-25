package dev.atomixsoft.solar_eclipse.core.event.types;

import dev.atomixsoft.solar_eclipse.core.event.Event;
import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public class SendPacketEvent extends Event {

    private final Packet m_Packet;

    public SendPacketEvent(Packet packet) {
        super("Send Packet Event");
        m_Packet = packet;
    }

    public Packet getPacket() {
        return m_Packet;
    }

}
