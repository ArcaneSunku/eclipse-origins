package dev.atomixsoft.solar_eclipse.client.graphics.ui.old;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;
import dev.atomixsoft.solar_eclipse.client.ClientThread;
import dev.atomixsoft.solar_eclipse.client.graphics.Texture;
import dev.atomixsoft.solar_eclipse.core.event.types.ShutdownEvent;
import dev.atomixsoft.solar_eclipse.core.utils.FileUtils;
import imgui.ImFont;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;

import java.util.HashMap;
import java.util.Map;

public class MainMenu {

    public enum MainMenuState {
        MOTD, Login, Register, Credits
    }

    private MainMenuState m_State;
    private ImFont m_Georgia;

    private ImString m_User, m_Password;
    private ImBoolean m_ShowPass;

    private final Map<String, Button> m_Buttons;

    public MainMenu() {
        m_Buttons = new HashMap<>();
        setup();
    }

    public void setup() {
        m_State = MainMenuState.MOTD;

        m_User = new ImString();
        m_Password = new ImString();
        m_ShowPass = new ImBoolean(false);

        m_Buttons.put("login", new Button("btn_menu_login"));
        m_Buttons.put("register", new Button("btn_menu_register"));
        m_Buttons.put("credits", new Button("btn_menu_credits"));
        m_Buttons.put("exit", new Button("btn_menu_exit"));
    }

    public void render() {
        ImGui.setCursorPos(38, 12);
        switch(m_State) {
            case MOTD -> {
                Texture main_menu = AssetLoader.GetTexture("ui_menu_main");
                ImGui.image(main_menu.getTextureId(), main_menu.getWidth(), main_menu.getHeight());

                ImGui.pushStyleColor(ImGuiCol.Border, 1, 1, 1, 0);
                ImGui.pushStyleColor(ImGuiCol.WindowBg, 1, 1, 1, 0);
                ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
                ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
                ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, 0);
                ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 0, 0);

                ImGui.setNextWindowPos(38 + main_menu.getWidth() / 8.5f, 75);
                ImGui.setNextWindowSize(main_menu.getWidth(), 200);
                String news = FileUtils.StringFromFile("client/news.txt");
                ImGui.begin("News", ImGuiWindowFlags.NoDecoration);
                float windowWidth = ImGui.getWindowSize().x;
                float textWidth = ImGui.calcTextSize(news).x;

                // calculate the indentation that centers the text on one line, relative
                // to window left, regardless of the `ImGuiStyleVar_WindowPadding` value
                float text_indentation = (windowWidth - textWidth) * 0.5f;

                // if text is too long to be drawn on one line, `text_indentation` can
                // become too small or even negative, so we check a minimum indentation
                float min_indentation = 55.0f;
                if (text_indentation <= min_indentation) {
                    text_indentation = min_indentation;
                }

                ImGui.pushTextWrapPos(windowWidth - text_indentation);
                ImGui.pushFont(m_Georgia);
                ImGui.text(news);
                ImGui.popFont();
                ImGui.popTextWrapPos();

                ImGui.end();

                ImGui.popStyleColor(2);
                ImGui.popStyleVar(4);
            }
            case Login -> {
                Texture main_menu = AssetLoader.GetTexture("ui_menu_login");
                ImGui.image(main_menu.getTextureId(), main_menu.getWidth(), main_menu.getHeight());

                ImGui.pushStyleColor(ImGuiCol.Border, 1, 1, 1, 0);
                ImGui.pushStyleColor(ImGuiCol.WindowBg, 1, 1, 1, 0);
                ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
                ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
                ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, 0);
                ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 0, 0);

                ImGui.setNextWindowSize(main_menu.getWidth() / 1.5f, main_menu.getHeight() / 4f);

                ImGui.begin("Login_Window", ImGuiWindowFlags.NoDecoration);
                ImGui.text("Username: ");
                ImGui.sameLine();
                ImGui.pushItemWidth(100);
                ImGui.inputText("##Username", m_User);
                ImGui.popItemWidth();

                ImGui.text("Password: ");
                ImGui.sameLine();
                int inputFlags = !m_ShowPass.get() ? ImGuiInputTextFlags.Password : ImGuiInputTextFlags.None;
                ImGui.pushItemWidth(100);
                ImGui.inputText("##Password", m_Password, inputFlags);
                ImGui.popItemWidth();
                ImGui.sameLine();
                ImGui.checkbox("Show Pass", m_ShowPass);

                ImGui.end();

                ImGui.popStyleColor(2);
                ImGui.popStyleVar(4);

//                ClientThread.set_size(785, 594);
//                ClientThread.set_scene("Test");
            }
            case Register -> {
                Texture main_menu = AssetLoader.GetTexture("ui_menu_register");
                ImGui.image(main_menu.getTextureId(), main_menu.getWidth(), main_menu.getHeight());
                ClientThread.set_size(785, 594);
                ClientThread.set_scene("Test");
            }
            case Credits -> {
                Texture main_menu = AssetLoader.GetTexture("ui_menu_credits");
                ImGui.image(main_menu.getTextureId(), main_menu.getWidth(), main_menu.getHeight());
            }
        }

        Button login_button = m_Buttons.get("login");
        if(login_button != null) {
            login_button.render("Login", 64, 287);

            if(login_button.getState() == Button.State.CLICKED)
                m_State = MainMenuState.Login;
        }

        Button register_button = m_Buttons.get("register");
        if(register_button != null) {
            register_button.render("Register", 64 + (11 + register_button.getWidth()), 287);

            if(register_button.getState() == Button.State.CLICKED)
                m_State = MainMenuState.Register;
        }

        Button credits_button = m_Buttons.get("credits");
        if(credits_button != null) {
            credits_button.render("Credits", 64 + (11 + credits_button.getWidth()) * 2, 287);

            if(credits_button.getState() == Button.State.CLICKED)
                m_State = MainMenuState.Credits;
        }

        Button exit_button = m_Buttons.get("exit");
        if(exit_button != null) {
            exit_button.render("Exit", 64 + (11 + exit_button.getWidth()) * 3, 287);

            if(exit_button.getState() == Button.State.CLICKED)
                ClientThread.eventBus().post(new ShutdownEvent("dev", false));
        }
    }

    public void dispose() {
        resetUI();
        m_Buttons.clear();
        m_Georgia = null;
    }

    public void resetUI() {
        if(m_State != MainMenuState.MOTD)
            m_State = MainMenuState.MOTD;
    }

    public void setFont(ImFont font) {
        m_Georgia = font;
    }

}
