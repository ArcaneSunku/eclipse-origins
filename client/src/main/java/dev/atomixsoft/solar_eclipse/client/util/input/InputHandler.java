package dev.atomixsoft.solar_eclipse.client.util.input;

import dev.atomixsoft.solar_eclipse.client.Client;
import dev.atomixsoft.solar_eclipse.client.ClientThread;
import dev.atomixsoft.solar_eclipse.client.config.Configuration;
import dev.atomixsoft.solar_eclipse.core.event.interfaces.EventListener;
import dev.atomixsoft.solar_eclipse.core.event.types.InputEvent;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

import static dev.atomixsoft.solar_eclipse.core.event.types.InputEvent.InputType;

/**
 * <p>Handles input interactions for the app, allows us to manage the key/button states after GLFW gives us feedback.</p>
 */
public class InputHandler implements EventListener <InputEvent>{
    private static InputHandler ms_Instance = null;

    private final Map<Integer, Key> m_Keys;
    private final Map<Integer, MButton> m_MButtons;

    private final Map<InputType, Integer> m_Bindings;

    public static InputHandler Instance() {
        if(ms_Instance == null) ms_Instance = new InputHandler();
        return ms_Instance;
    }

    private InputHandler() {
        m_Keys = new HashMap<>();
        m_MButtons = new HashMap<>();
        m_Bindings = new HashMap<>();

        init();
    }

    private void init() {
        updateBindings(Client.ConfigInfo);

        for(var k = GLFW_KEY_SPACE; k < GLFW_KEY_LAST; ++k)
            m_Keys.putIfAbsent(k, new Key());

        for(var mb = GLFW_MOUSE_BUTTON_1; mb < GLFW_MOUSE_BUTTON_LAST; ++mb)
            m_MButtons.putIfAbsent(mb, new MButton());
    }

    public void process() {
        m_Keys.values().forEach(Key::update);
        m_MButtons.values().forEach(MButton::update);
    }

    public void updateBindings(Configuration conf) {
        for(InputType type : InputType.values()) {
            switch (type) {
                case PICKUP -> m_Bindings.put(type, ConvertInputToGLFW(conf.getPickUpKey()));
                case ACTION -> m_Bindings.put(type, ConvertInputToGLFW(conf.getActionKey()));
                case CANCEL -> m_Bindings.put(type, ConvertInputToGLFW(conf.getCancelKey()));
                case RUN    -> m_Bindings.put(type, ConvertInputToGLFW(conf.getRunKey()));
                case UP     -> m_Bindings.put(type, ConvertInputToGLFW(conf.getUpKey()));
                case DOWN   -> m_Bindings.put(type, ConvertInputToGLFW(conf.getDownKey()));
                case LEFT   -> m_Bindings.put(type, ConvertInputToGLFW(conf.getLeftKey()));
                case RIGHT  -> m_Bindings.put(type, ConvertInputToGLFW(conf.getRightKey()));
            }
        }
    }

    @Override
    public void handleEvent(InputEvent event) {
        Key mapped = Instance().m_Keys.get(m_Bindings.get(event.getInputType()));
        if(mapped == null) return;

        ClientThread.log().debug(String.format("Input Event[%s, %s, %b]", event.getUserID(), event.getInputType(), event.getPressData()));

        mapped.toggle(event.getPressData());
        process();

        event.handled = true;
    }

    public Map<InputType, Integer> getBindings() {
        return m_Bindings;
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
        if(!ms_Instance.m_Bindings.containsValue(key) || mods == GLFW_MOD_ALT) {
            Key mapped = Instance().m_Keys.get(key);
            if(mapped == null) return;

            mapped.toggle(action != GLFW_RELEASE);
            return;
        }

        for(InputType type : InputType.values()) {
            if(ms_Instance.m_Bindings.get(type) == key) {
                InputEvent event = new InputEvent("ArcaneSunku", type, action != GLFW_RELEASE);
                ClientThread.eventBus().post(event);
                return;
            }
        }
    }

    public static void mouse_button_callback(long window, int button, int action, int mods) {
        MButton mapped = Instance().m_MButtons.get(button);
        if(mapped == null) return;

        mapped.toggle(action != GLFW_RELEASE);
    }

    public int ConvertInputToGLFW(String inputName) {
        return switch (inputName) {
            case "A" -> GLFW_KEY_A;
            case "B" -> GLFW_KEY_B;
            case "C" -> GLFW_KEY_C;
            case "D" -> GLFW_KEY_D;
            case "E" -> GLFW_KEY_E;
            case "F" -> GLFW_KEY_F;
            case "G" -> GLFW_KEY_G;
            case "H" -> GLFW_KEY_H;
            case "I" -> GLFW_KEY_I;
            case "J" -> GLFW_KEY_J;
            case "K" -> GLFW_KEY_K;
            case "L" -> GLFW_KEY_L;
            case "M" -> GLFW_KEY_M;
            case "N" -> GLFW_KEY_N;
            case "O" -> GLFW_KEY_O;
            case "P" -> GLFW_KEY_P;
            case "Q" -> GLFW_KEY_Q;
            case "R" -> GLFW_KEY_R;
            case "S" -> GLFW_KEY_S;
            case "T" -> GLFW_KEY_T;
            case "U" -> GLFW_KEY_U;
            case "V" -> GLFW_KEY_V;
            case "W" -> GLFW_KEY_W;
            case "X" -> GLFW_KEY_X;
            case "Y" -> GLFW_KEY_Y;
            case "Z" -> GLFW_KEY_Z;
            case "1" -> GLFW_KEY_1;
            case "2" -> GLFW_KEY_2;
            case "3" -> GLFW_KEY_3;
            case "4" -> GLFW_KEY_4;
            case "5" -> GLFW_KEY_5;
            case "6" -> GLFW_KEY_6;
            case "7" -> GLFW_KEY_7;
            case "8" -> GLFW_KEY_8;
            case "9" -> GLFW_KEY_9;
            case "0" -> GLFW_KEY_0;
            case "Escape" -> GLFW_KEY_ESCAPE;
            case "Enter" -> GLFW_KEY_ENTER;
            case "Tab" -> GLFW_KEY_TAB;
            case "Backspace" -> GLFW_KEY_BACKSPACE;
            case "Insert" -> GLFW_KEY_INSERT;
            case "Delete" -> GLFW_KEY_DELETE;
            case "Right" -> GLFW_KEY_RIGHT;
            case "Left" -> GLFW_KEY_LEFT;
            case "Down" -> GLFW_KEY_DOWN;
            case "Up" -> GLFW_KEY_UP;
            case "PageUp" -> GLFW_KEY_PAGE_UP;
            case "PageDown" -> GLFW_KEY_PAGE_DOWN;
            case "Home" -> GLFW_KEY_HOME;
            case "End" -> GLFW_KEY_END;
            case "CapsLock" -> GLFW_KEY_CAPS_LOCK;
            case "ScrollLock" -> GLFW_KEY_SCROLL_LOCK;
            case "NumLock" -> GLFW_KEY_NUM_LOCK;
            case "PrintScreen" -> GLFW_KEY_PRINT_SCREEN;
            case "Pause" -> GLFW_KEY_PAUSE;
            case "F1" -> GLFW_KEY_F1;
            case "F2" -> GLFW_KEY_F2;
            case "F3" -> GLFW_KEY_F3;
            case "F4" -> GLFW_KEY_F4;
            case "F5" -> GLFW_KEY_F5;
            case "F6" -> GLFW_KEY_F6;
            case "F7" -> GLFW_KEY_F7;
            case "F8" -> GLFW_KEY_F8;
            case "F9" -> GLFW_KEY_F9;
            case "F10" -> GLFW_KEY_F10;
            case "F11" -> GLFW_KEY_F11;
            case "F12" -> GLFW_KEY_F12;
            case "F13" -> GLFW_KEY_F13;
            case "F14" -> GLFW_KEY_F14;
            case "F15" -> GLFW_KEY_F15;
            case "F16" -> GLFW_KEY_F16;
            case "F17" -> GLFW_KEY_F17;
            case "F18" -> GLFW_KEY_F18;
            case "F19" -> GLFW_KEY_F19;
            case "F20" -> GLFW_KEY_F20;
            case "F21" -> GLFW_KEY_F21;
            case "F22" -> GLFW_KEY_F22;
            case "F23" -> GLFW_KEY_F23;
            case "F24" -> GLFW_KEY_F24;
            case "F25" -> GLFW_KEY_F25;
            case "KP0" -> GLFW_KEY_KP_0;
            case "KP1" -> GLFW_KEY_KP_1;
            case "KP2" -> GLFW_KEY_KP_2;
            case "KP3" -> GLFW_KEY_KP_3;
            case "KP4" -> GLFW_KEY_KP_4;
            case "KP5" -> GLFW_KEY_KP_5;
            case "KP6" -> GLFW_KEY_KP_6;
            case "KP7" -> GLFW_KEY_KP_7;
            case "KP8" -> GLFW_KEY_KP_8;
            case "KP9" -> GLFW_KEY_KP_9;
            case "KPDecimal" -> GLFW_KEY_KP_DECIMAL;
            case "KPDivide" -> GLFW_KEY_KP_DIVIDE;
            case "KPMultiply" -> GLFW_KEY_KP_MULTIPLY;
            case "KPSubtract" -> GLFW_KEY_KP_SUBTRACT;
            case "KPAdd" -> GLFW_KEY_KP_ADD;
            case "KPEnter" -> GLFW_KEY_KP_ENTER;
            case "KPEqual" -> GLFW_KEY_KP_EQUAL;
            case "LShift" -> GLFW_KEY_LEFT_SHIFT;
            case "LControl" -> GLFW_KEY_LEFT_CONTROL;
            case "LAlt" -> GLFW_KEY_LEFT_ALT;
            case "LSuper" -> GLFW_KEY_LEFT_SUPER;
            case "RShift" -> GLFW_KEY_RIGHT_SHIFT;
            case "RControl" -> GLFW_KEY_RIGHT_CONTROL;
            case "RAlt" -> GLFW_KEY_RIGHT_ALT;
            case "RSuper" -> GLFW_KEY_RIGHT_SUPER;
            case "Menu" -> GLFW_KEY_MENU;
            case "GraveAccent" -> GLFW_KEY_GRAVE_ACCENT;
            case "World1" -> GLFW_KEY_WORLD_1;
            case "World2" -> GLFW_KEY_WORLD_2;
            case "Minus" -> GLFW_KEY_MINUS;
            case "Equal" -> GLFW_KEY_EQUAL;
            case "LeftBracket" -> GLFW_KEY_LEFT_BRACKET;
            case "RightBracket" -> GLFW_KEY_RIGHT_BRACKET;
            case "Backslash" -> GLFW_KEY_BACKSLASH;
            case "Semicolon" -> GLFW_KEY_SEMICOLON;
            case "Apostrophe" -> GLFW_KEY_APOSTROPHE;
            case "Comma" -> GLFW_KEY_COMMA;
            case "Period" -> GLFW_KEY_PERIOD;
            case "Slash" -> GLFW_KEY_SLASH;
            default -> GLFW_KEY_UNKNOWN;
        };
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
