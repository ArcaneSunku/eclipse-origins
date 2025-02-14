package dev.atomixsoft.solar_eclipse.client.scene;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;
import dev.atomixsoft.solar_eclipse.client.ClientThread;
import dev.atomixsoft.solar_eclipse.client.graphics.Texture;
import dev.atomixsoft.solar_eclipse.client.util.input.Controller;
import dev.atomixsoft.solar_eclipse.core.event.types.InputEvent;
import dev.atomixsoft.solar_eclipse.core.event.types.ShutdownEvent;

import dev.atomixsoft.solar_eclipse.core.utils.FileUtils;
import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

/**
 * <p>Represents the Main Menu of our game, from here you'll see everything involving the login screen for the client.</p>
 */
public class MenuScene extends SceneAdapter {

    private enum UIState {
        Main, Login, Register, Credits
    }

    private UIState uiState;
    private ImFont georgia;

    @Override
    public void show() {
        uiState = UIState.Main;

        ImGuiIO io = ImGui.getIO();
        if(georgia == null)
            georgia = io.getFonts().addFontFromFileTTF("client/assets/fonts/georgia.ttf", 16);
    }

    @Override
    public void hide() {
        uiState = UIState.Main;
    }

    @Override
    public void update(Controller input, double dt) {
        if(input.justPressed(InputEvent.InputType.CANCEL) && uiState != UIState.Main)
            uiState = UIState.Main;
    }

    private int exitFlag = 0, loginFlag = 0;
    private int registerFlag = 0, creditFlag = 0;

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
                ClientThread.set_size(785, 594);
                ClientThread.set_scene("Test");
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

        Texture[] btn_login = new Texture[] {
                AssetLoader.GetTexture("btn_menu_login"),
                AssetLoader.GetTexture("btn_menu_login_hover"),
                AssetLoader.GetTexture("btn_menu_login_click"),
        };

        ImGui.setCursorPos(64, 287);
        if(ImGui.imageButton("Login", btn_login[loginFlag].getTextureId(), btn_login[loginFlag].getWidth(), btn_login[loginFlag].getHeight())) {
            loginFlag = 2;
            uiState = UIState.Login;
        } else {
            if(ImGui.isItemHovered()) loginFlag = 1;
            else if(!ImGui.isItemHovered()) loginFlag = 0;
        }

        Texture[] btn_register = new Texture[] {
                AssetLoader.GetTexture("btn_menu_register"),
                AssetLoader.GetTexture("btn_menu_register_hover"),
                AssetLoader.GetTexture("btn_menu_register_click"),
        };

        ImGui.setCursorPos(64 + (11 + btn_register[registerFlag].getWidth()), 287);
        if(ImGui.imageButton("Register", btn_register[registerFlag].getTextureId(), btn_register[registerFlag].getWidth(), btn_register[registerFlag].getHeight())) {
            registerFlag = 2;
            uiState = UIState.Register;
        } else {
            if(ImGui.isItemHovered()) registerFlag = 1;
            else if(!ImGui.isItemHovered()) registerFlag = 0;
        }

        Texture[] btn_credits = new Texture[] {
                AssetLoader.GetTexture("btn_menu_credits"),
                AssetLoader.GetTexture("btn_menu_credits_hover"),
                AssetLoader.GetTexture("btn_menu_credits_click"),
        };

        ImGui.setCursorPos(64 + (11 + btn_credits[creditFlag].getWidth()) * 2, 287);
        if(ImGui.imageButton("Credits", btn_credits[creditFlag].getTextureId(), btn_credits[creditFlag].getWidth(), btn_credits[creditFlag].getHeight())) {
            creditFlag = 2;
            uiState = UIState.Credits;
        } else {
            if(ImGui.isItemHovered()) creditFlag = 1;
            else if(!ImGui.isItemHovered()) creditFlag = 0;
        }

        Texture[] btn_exit = new Texture[] {
                AssetLoader.GetTexture("btn_menu_exit"),
                AssetLoader.GetTexture("btn_menu_exit_hover"),
                AssetLoader.GetTexture("btn_menu_exit_click"),
        };

        ImGui.setCursorPos(64 + (11 + btn_exit[exitFlag].getWidth()) * 3, 287);
        if(ImGui.imageButton("Exit", btn_exit[exitFlag].getTextureId(), btn_exit[exitFlag].getWidth(), btn_exit[exitFlag].getHeight())) {
            exitFlag = 2;
            ClientThread.eventBus().post(new ShutdownEvent("dev", false));
        } else {
            if(ImGui.isItemHovered()) exitFlag = 1;
            else if(!ImGui.isItemHovered()) exitFlag = 0;
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
