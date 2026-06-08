package dev.atomixsoft.solar_eclipse.client.graphics.ui.ingame;

import dev.atomixsoft.solar_eclipse.client.graphics.Texture;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class Hotbar {

    private final Texture m_Texture;
    private final int m_XPos, m_YPos;

    public Hotbar(Texture texture, int x, int y) {
        m_Texture = texture;

        m_XPos = x;
        m_YPos = y;
    }

    public void render() {
        ImGui.setNextWindowPos(m_XPos, m_YPos);
        ImGui.setNextWindowSize(m_Texture.getWidth(), m_Texture.getHeight());

        ImGui.begin("Hotbar", ImGuiWindowFlags.NoDecoration);
        ImGui.image(m_Texture.getTextureId(), ImGui.getContentRegionAvail());
        ImGui.end();
    }

}
