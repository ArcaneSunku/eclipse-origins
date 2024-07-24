package dev.atomixsoft.solar_eclipse.core.event.types;

import dev.atomixsoft.solar_eclipse.core.event.Event;

public class InputEvent extends Event {

    public enum InputType {
        PICKUP, ACTION, CANCEL, RUN,
        UP, DOWN, LEFT, RIGHT
    }

    private final int m_UserID;
    private final InputType m_InputData;

    public InputEvent(int user, InputType input) {
        super("Input Event");

        m_UserID = user;
        m_InputData = input;
    }

    public int getUserID() {
        return m_UserID;
    }

    public InputType getInputData() {
        return m_InputData;
    }

}
