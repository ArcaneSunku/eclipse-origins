package dev.atomixsoft.solar_eclipse.server.console.events;

import dev.atomixsoft.solar_eclipse.core.event.Event;

public class CommandEvent extends Event {

    private final String m_CMD;

    public CommandEvent(String cmd) {
        super("Command Event");
        m_CMD = cmd;
    }

    public String getCommand() {
        return m_CMD;
    }

}
