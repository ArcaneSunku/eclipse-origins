package dev.atomixsoft.solar_eclipse.client.util.input;

import dev.atomixsoft.solar_eclipse.client.Client;
import dev.atomixsoft.solar_eclipse.client.config.Configuration;
import static dev.atomixsoft.solar_eclipse.core.event.types.InputEvent.InputType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Lets us map Mouse/Keyboard to predefined key functions. Think of it as the average keybinding situation.</p>
 */
public class Controller {
    private final InputHandler m_In;
    private final Map<String, Integer> m_InputMap;


    public Controller() {
        m_In = InputHandler.Instance();
        m_InputMap = new HashMap<>();
        updateBindings();
    }

    public void updateBindings() {
        for(InputType type : InputType.values()) {
            switch (type) {
                case PICKUP -> addBinding(type, m_In.ConvertInputToGLFW(Client.ConfigInfo.getPickUpKey()));
                case ACTION -> addBinding(type, m_In.ConvertInputToGLFW(Client.ConfigInfo.getActionKey()));
                case CANCEL -> addBinding(type, m_In.ConvertInputToGLFW(Client.ConfigInfo.getCancelKey()));
                case RUN    -> addBinding(type, m_In.ConvertInputToGLFW(Client.ConfigInfo.getRunKey()));
                case UP     -> addBinding(type, m_In.ConvertInputToGLFW(Client.ConfigInfo.getUpKey()));
                case DOWN   -> addBinding(type, m_In.ConvertInputToGLFW(Client.ConfigInfo.getDownKey()));
                case LEFT   -> addBinding(type, m_In.ConvertInputToGLFW(Client.ConfigInfo.getLeftKey()));
                case RIGHT  -> addBinding(type, m_In.ConvertInputToGLFW(Client.ConfigInfo.getRightKey()));
            }
        }
    }

    public boolean isPressed(InputType controlName) {
        if(!m_InputMap.containsKey(controlName.name())) return false;
        return m_In.isKeyDown(m_InputMap.get(controlName.name()));
    }

    public boolean justPressed(InputType controlName) {
        if(!m_InputMap.containsKey(controlName.name())) return false;
        return m_In.keyJustPressed(m_InputMap.get(controlName.name()));
    }

    private void addBinding(InputType controlName, int input) {
        m_InputMap.put(controlName.name(), input);
    }
}
