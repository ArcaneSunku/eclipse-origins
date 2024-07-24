package dev.atomixsoft.solar_eclipse.client.util.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Lets us map Mouse/Keyboard to predefined key functions. Think of it as the average keybinding situation.</p>
 */
public class Controller {
    private final InputHandler m_In;
    private final Map<String, List<Integer>> m_InputMap;


    public Controller() {
        m_In = InputHandler.Instance();
        m_InputMap = new HashMap<>();
    }

    public boolean isPressed(String controlName) {
        if(!m_InputMap.containsKey(controlName)) return false;

        List<Integer> bindings = m_InputMap.get(controlName);
        for(Integer key : bindings)
            if(m_In.isKeyDown(key)) return m_In.isKeyDown(key);

        return false;
    }

    public boolean justPressed(String controlName) {
        if(!m_InputMap.containsKey(controlName)) return false;

        List<Integer> bindings = m_InputMap.get(controlName);
        for(Integer key : bindings)
            if(m_In.keyJustPressed(key)) return m_In.keyJustPressed(key);

        return false;
    }

    public void addBinding(String controlName, int input) {
        m_InputMap.putIfAbsent(controlName, new ArrayList<>());
        List<Integer> bindings = m_InputMap.get(controlName);

        for(int keyBind : bindings) {
            if (keyBind == input) {
                System.out.println(controlName + " is already bound with " + input + "!");
                return;
            }
        }

        bindings.add(input);
    }
}
