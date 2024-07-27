package dev.atomixsoft.solar_eclipse.core.event.types;

import dev.atomixsoft.solar_eclipse.core.event.Event;

public class InputEvent extends Event {

    public enum InputType {
        PICKUP, ACTION, CANCEL, RUN,
        UP, DOWN, LEFT, RIGHT
    }

    private final String m_UserID;
    private final InputType m_InputData;
    private final boolean m_PressData;

    public InputEvent(String user, InputType input, boolean pressed) {
        super("Input Event");

        m_UserID = user;
        m_InputData = input;
        m_PressData = pressed;
    }

    public String getUserID() {
        return m_UserID;
    }

    public InputType getInputData() {
        return m_InputData;
    }

    public boolean getPressData() {
        return m_PressData;
    }

}
