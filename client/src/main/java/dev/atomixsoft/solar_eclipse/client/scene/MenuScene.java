package dev.atomixsoft.solar_eclipse.client.scene;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;
import dev.atomixsoft.solar_eclipse.client.ClientThread;
import dev.atomixsoft.solar_eclipse.client.graphics.Texture;
import dev.atomixsoft.solar_eclipse.client.graphics.ui.Button;
import dev.atomixsoft.solar_eclipse.client.util.input.Controller;
import dev.atomixsoft.solar_eclipse.core.event.types.InputEvent;
import dev.atomixsoft.solar_eclipse.core.event.types.ShutdownEvent;

import dev.atomixsoft.solar_eclipse.core.utils.FileUtils;
import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Represents the Main Menu of our game, from here you'll see everything involving the login screen for the client.</p>
 */
public class MenuScene extends SceneAdapter {

    private enum UIState {
        Main, Login, Register, Credits
    }

    private UIState uiState;
    private ImFont georgia;

    private ImString userName;
    private ImString password;
    private ImBoolean showPass;

    private Map<String, Button> buttons;

    @Override
    public void show() {
        uiState = UIState.Main;

        userName = new ImString();
        password = new ImString();
        showPass = new ImBoolean(false);

        buttons = new HashMap<>();

        buttons.put("login", new Button("btn_menu_login"));
        buttons.put("register", new Button("btn_menu_register"));
        buttons.put("credits", new Button("btn_menu_credits"));
        buttons.put("exit", new Button("btn_menu_exit"));

        ImGuiIO io = ImGui.getIO();
        if(georgia == null)
            georgia = io.getFonts().addFontFromFileTTF("client/assets/fonts/georgia.ttf", 16);
    }

    @Override
    public void hide() {
        uiState = UIState.Main;
        buttons.clear();
    }

    @Override
    public void update(Controller input, double dt) {
        if(input.justPressed(InputEvent.InputType.CANCEL) && uiState != UIState.Main)
            uiState = UIState.Main;
    }

    private void renderUI(UIState state) {
        ImGui.setCursorPos(38, 12);
        switch(state) {
            case Main -> {
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
                ImGui.pushFont(georgia);
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
                ImGui.inputText("##Username", userName);
                ImGui.popItemWidth();

                ImGui.text("Password: ");
                ImGui.sameLine();
                int inputFlags = !showPass.get() ? ImGuiInputTextFlags.Password : ImGuiInputTextFlags.None;
                ImGui.pushItemWidth(100);
                ImGui.inputText("##Password", password, inputFlags);
                ImGui.popItemWidth();
                ImGui.sameLine();
                ImGui.checkbox("Show Pass", showPass);

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

        Button login_button = buttons.get("login");
        if(login_button != null) {
            login_button.render("Login", 64, 287);

            if(login_button.getState() == Button.State.CLICKED)
                uiState = UIState.Login;
        }

        Button register_button = buttons.get("register");
        if(register_button != null) {
            register_button.render("Register", 64 + (11 + register_button.getWidth()), 287);

            if(register_button.getState() == Button.State.CLICKED)
                uiState = UIState.Register;
        }

        Button credits_button = buttons.get("credits");
        if(credits_button != null) {
            credits_button.render("Credits", 64 + (11 + credits_button.getWidth()) * 2, 287);

            if(credits_button.getState() == Button.State.CLICKED)
                uiState = UIState.Credits;
        }

        Button exit_button = buttons.get("exit");
        if(exit_button != null) {
            exit_button.render("Exit", 64 + (11 + exit_button.getWidth()) * 3, 287);

            if(exit_button.getState() == Button.State.CLICKED)
                ClientThread.eventBus().post(new ShutdownEvent("dev", false));
        }
    }

    @Override
    public void imgui() {
        Texture menu_bg = AssetLoader.GetTexture("ui_menu_background");

        ImGui.pushStyleColor(ImGuiCol.Border, 1, 1, 1, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 0, 0);

        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(menu_bg.getWidth(), menu_bg.getHeight());

        ImGui.begin("Menu_Scene", ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoBringToFrontOnFocus);
        ImGui.image(menu_bg.getTextureId(), ImGui.getContentRegionAvail());
        ImGui.isItemHovered();

        renderUI(uiState);
        ImGui.end();

        ImGui.popStyleColor();
        ImGui.popStyleVar(4);
    }

}
