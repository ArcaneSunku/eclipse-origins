package dev.atomixsoft.solar_eclipse.client.util.input;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

/**
 * <p>Handles input interactions for the app, allows us to manage the key/button states after GLFW gives us feedback.</p>
 */
public class InputHandler {
    private static InputHandler ms_Instance = null;

    private final Map<Integer, Key> m_Keys;
    private final Map<Integer, MButton> m_MButtons;

    public static InputHandler Instance() {
        if(ms_Instance == null) ms_Instance = new InputHandler();
        return ms_Instance;
    }

    private InputHandler() {
        m_Keys = new HashMap<>();
        m_MButtons = new HashMap<>();

        init();
    }

    private void init() {
        for(var k = GLFW_KEY_SPACE; k < GLFW_KEY_LAST; ++k)
            m_Keys.putIfAbsent(k, new Key());

        for(var mb = GLFW_MOUSE_BUTTON_1; mb < GLFW_MOUSE_BUTTON_LAST; ++mb)
            m_MButtons.putIfAbsent(mb, new MButton());
    }

    public void process() {
        m_Keys.values().forEach(Key::update);
        m_MButtons.values().forEach(MButton::update);
    }

    public boolean isKeyDown(int key) {
        return m_Keys.get(key).down;
    }

    public boolean mouseButtonJustPressed(int key) {
        return m_MButtons.get(key).justClicked;
    }

    public boolean isMouseButtonDown(int mb) {
        return m_MButtons.get(mb).down;
    }

    public boolean keyJustPressed(int key) {
        return m_Keys.get(key).justPressed;
    }

    public static void key_callback(long window, int key, int scancode, int action, int mods) {
        Key mapped = Instance().m_Keys.get(key);
        if(mapped == null) return;

        mapped.toggle(action != GLFW_RELEASE);
    }

    public static void mouse_button_callback(long window, int button, int action, int mods) {
        MButton mapped = Instance().m_MButtons.get(button);
        if(mapped == null) return;

        mapped.toggle(action != GLFW_RELEASE);
    }

    /**
     * <p>Basic class for a Keyboard key, lets us know if its just been pressed or is held.</p>
     */
    private static class Key {
        int presses = 0, absorbs = 0;
        boolean down = false, justPressed = false;

        void toggle(boolean pressed) {
            if(pressed != down)
                down = pressed;

            if(pressed) ++presses;
        }

        void update() {
            if(absorbs < presses) {
                justPressed = true;
                ++absorbs;
            } else {
                justPressed = false;
            }
        }
    }

    /**
     * <p>Basic class for a Mouse Button, lets us know if its just been clicked or is held.</p>
     */
    private static class MButton {
        int presses = 0, absorbs = 0;
        boolean down = false, justClicked = false;

        void toggle(boolean pressed) {
            if(pressed != down)
                down = pressed;

            if(pressed) ++presses;
        }

        void update() {
            if(absorbs < presses) {
                justClicked = true;
                ++absorbs;
            } else {
                justClicked = false;
            }
        }
    }
}
