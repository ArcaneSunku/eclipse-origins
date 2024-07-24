package dev.atomixsoft.solar_eclipse.core.event;

public abstract class Event {

    private final String m_Id;

    public boolean handled;

    public Event(String id) {
        m_Id = id;
        handled = false;
    }

    public String getID() {
        return m_Id;
    }

}
