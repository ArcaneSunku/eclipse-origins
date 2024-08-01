package dev.atomixsoft.solar_eclipse.core.event.types;

import dev.atomixsoft.solar_eclipse.core.event.Event;

public class InputEvent extends Event {

    public enum InputType {
        PICKUP, ACTION, CANCEL, RUN,
        UP, DOWN, LEFT, RIGHT
    }

    private final String m_UserID;
    private final InputType m_InputType;
    private final boolean m_Pressed;

    public InputEvent(String user, InputType input, boolean pressed) {
        super("Input Event");

        m_UserID = user;
        m_InputType = input;
        m_Pressed = pressed;
    }

    public String getUserID() {
        return m_UserID;
    }

    public InputType getInputType() {
        return m_InputType;
    }

    public boolean getPressData() {
        return m_Pressed;
    }

}
