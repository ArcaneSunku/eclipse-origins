package dev.atomixsoft.solar_eclipse.core.event.types;

import dev.atomixsoft.solar_eclipse.core.event.Event;

public class ShutdownEvent extends Event {

    private final String m_SenderID;
    private final boolean m_Server;

    public ShutdownEvent(String sender, boolean server) {
        super("Shutdown Event");
        m_Server = server;
        m_SenderID = !m_Server ? "c" + sender : "s" + sender;
    }

    public String getSender() {
        return m_SenderID;
    }

    public boolean isServer() {
        return m_Server;
    }
}
