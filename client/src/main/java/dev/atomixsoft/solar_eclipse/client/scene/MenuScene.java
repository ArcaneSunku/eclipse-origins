package dev.atomixsoft.solar_eclipse.client.scene;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;
import dev.atomixsoft.solar_eclipse.client.Client;
import dev.atomixsoft.solar_eclipse.client.ClientThread;
import dev.atomixsoft.solar_eclipse.client.audio.AudioMaster;
import dev.atomixsoft.solar_eclipse.client.audio.AudioSource;
import dev.atomixsoft.solar_eclipse.client.graphics.Texture;
import dev.atomixsoft.solar_eclipse.client.graphics.ui.MainMenu;
import dev.atomixsoft.solar_eclipse.client.util.input.Controller;
import dev.atomixsoft.solar_eclipse.core.event.types.InputEvent;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.CharacterListResponse;
import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

/**
 * <p>Represents the Main Menu of our game, from here you'll see everything involving the login screen for the client.</p>
 */
public class MenuScene extends SceneAdapter {

    private MainMenu m_MainMenu;
    private AudioSource m_MainMenuSource;

    @Override
    public void show() {
        m_MainMenu = new MainMenu();

        m_MainMenuSource = new AudioSource();

        if(Boolean.parseBoolean(Client.ConfigInfo.getMusic())) {
            int mainMenuMusic = AudioMaster.LoadMusic("reddwarf.mid", "client/assets/music/" + Client.ConfigInfo.getMenuMusic());
            m_MainMenuSource.start(mainMenuMusic);
            m_MainMenuSource.setVolume(25);
            m_MainMenuSource.setLooping(true);
        }
    }

    @Override
    public void hide() {
        m_MainMenu.dispose();
        m_MainMenuSource.dispose();
    }

    @Override
    public void update(Controller input, double dt) {
        if(input.justPressed(InputEvent.InputType.ESCAPE))
            m_MainMenu.resetUI();

        if(m_MainMenu.requestedSceneChange()) {
            ClientThread.set_scene(m_MainMenu.getNextScene());
            m_MainMenu.resetSceneChangeFlags();
        }
    }

    @Override
    public void handlePacket(Packet packet) {
        switch(packet) {
            case CharacterListResponse p -> {
                m_MainMenu.applyCharacterList(p);
            }

            default -> {}
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

        m_MainMenu.render();
        ImGui.end();

        ImGui.popStyleColor();
        ImGui.popStyleVar(4);
    }

}
