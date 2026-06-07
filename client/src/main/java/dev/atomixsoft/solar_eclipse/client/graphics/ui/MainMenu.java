package dev.atomixsoft.solar_eclipse.client.graphics.ui;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;
import dev.atomixsoft.solar_eclipse.client.ClientThread;
import dev.atomixsoft.solar_eclipse.client.graphics.Texture;
import dev.atomixsoft.solar_eclipse.client.util.ImGuiFonts;
import dev.atomixsoft.solar_eclipse.core.event.types.SendPacketEvent;
import dev.atomixsoft.solar_eclipse.core.event.types.ShutdownEvent;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.LoginRequest;
import dev.atomixsoft.solar_eclipse.core.utils.FileUtils;
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
        News, Login, Register, Credits
    }

    private MainMenuState m_State;

    private ImString m_User, m_Password;
    private ImBoolean m_ShowPass;

    private final Map<String, Button> m_Buttons;

    private boolean m_ChangeSceneRequest;
    private String m_NextScene;

    public MainMenu() {
        m_Buttons = new HashMap<>();
        setup();
    }

    public void setup() {
        m_State = MainMenuState.News;

        m_User = new ImString();
        m_Password = new ImString();
        m_ShowPass = new ImBoolean(false);

        m_Buttons.put("login", new Button("btn_menu_login"));
        m_Buttons.put("register", new Button("btn_menu_register"));
        m_Buttons.put("credits", new Button("btn_menu_credits"));
        m_Buttons.put("exit", new Button("btn_menu_exit"));

        resetSceneChangeFlags();
    }

    public void resetSceneChangeFlags() {
        m_ChangeSceneRequest = false;
        m_NextScene = null;
    }

    public void render() {
        ImGui.setCursorPos(38, 12);
        switch(m_State) {
            case News -> {
                Texture main_menu = AssetLoader.GetTexture("ui_menu_main");
                ImGui.image(main_menu.getTextureId(), main_menu.getWidth(), main_menu.getHeight());

                ImGui.pushStyleColor(ImGuiCol.Border, 1, 1, 1, 0);
                ImGui.pushStyleColor(ImGuiCol.WindowBg, 1, 1, 1, 0);
                ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
                ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
                ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, 0);
                ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 0, 0);

                ImGui.setNextWindowPos(49, 70);
                ImGui.setNextWindowSize(main_menu.getWidth(), main_menu.getHeight());

                String news = FileUtils.StringFromFile("client/news.txt");
                ImGui.begin("News", ImGuiWindowFlags.NoDecoration);
                ImGui.pushFont(ImGuiFonts.GetFont("georgiab"));

                float baseX = 20.0f;
                float wrapWidth = ImGui.getWindowSizeX() - baseX * 3.5f;

                ImGui.setCursorPosX(baseX);
                for(String paragraph : news.split("\\R")) {
                    if(paragraph.isBlank()) {
                        ImGui.spacing();
                        continue;
                    }

                    drawCenteredWrappedText(paragraph, baseX, wrapWidth);
                }

                ImGui.popFont();
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
                ImGui.setNextWindowPos(main_menu.getWidth() / 3.5f, main_menu.getHeight() / 2f);

                ImGui.begin("Login_Window", ImGuiWindowFlags.NoDecoration);

                ImGui.text("Username: ");
                ImGui.sameLine();
                ImGui.pushItemWidth(100);
                ImGui.inputText("##Username", m_User);
                ImGui.popItemWidth();

                ImGui.text("Password:  ");
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

//                m_ChangeSceneRequest = true;
//                m_NextScene = "Test";

                sendLoginRequest();
                m_State = MainMenuState.Login;
            }
            case Credits -> {
                Texture main_menu = AssetLoader.GetTexture("ui_menu_credits");
                ImGui.image(main_menu.getTextureId(), main_menu.getWidth(), main_menu.getHeight());
            }
        }

        Button login_button = m_Buttons.get("login");
        if(login_button != null) {
            login_button.render("Login", 64, 287);

            if(login_button.getState() == Button.State.CLICKED) {
                if(m_State == MainMenuState.Login) {
                    sendLoginRequest();
                } else {
                    m_State = MainMenuState.Login;
                }
            }
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

    private void sendLoginRequest() {
        String username = m_User.get().trim();
        String password = m_Password.get();

        if(username.isBlank())
            username = "Dev";

        ClientThread.eventBus().post(new SendPacketEvent(new LoginRequest(username, password)));
    }

    private void drawCenteredWrappedText(String text, float baseX, float wrapWidth) {
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();

        for(String word : words) {
            String testLine = line.isEmpty() ? word : line + " " + word;
            float testWidth = ImGui.calcTextSize(testLine).x;

            if(testWidth > wrapWidth && !line.isEmpty()) {
                drawCenteredLine(line.toString(), baseX, wrapWidth);
                line.setLength(0);
                line.append(word);
            } else {
                line.setLength(0);
                line.append(testLine);
            }
        }

        if(!line.isEmpty())
            drawCenteredLine(line.toString(), baseX, wrapWidth);
    }

    private void drawCenteredLine(String line, float baseX, float wrapWidth) {
        float textWidth = ImGui.calcTextSize(line).x;
        float startX = baseX + Math.max((wrapWidth - textWidth) * 0.5f, 0.0f);

        ImGui.setCursorPosX(startX);
        ImGui.text(line);
    }

    public void dispose() {
        resetUI();
        m_Buttons.clear();
    }

    public void resetUI() {
        if(m_State != MainMenuState.News)
            m_State = MainMenuState.News;
    }

    public boolean requestedSceneChange() {
        return m_ChangeSceneRequest;
    }

    public String getNextScene() {
        return m_NextScene;
    }

}
