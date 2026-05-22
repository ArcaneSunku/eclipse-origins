package dev.atomixsoft.solar_eclipse.client.graphics.ui.old;

import dev.atomixsoft.solar_eclipse.client.graphics.Texture;
import dev.atomixsoft.solar_eclipse.core.game.character.Character;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class InventoryMenu {

    private final Texture m_Texture;
    private final int m_X, m_Y;

    public InventoryMenu(Texture texture, int x, int y) {
        m_Texture = texture;
        m_X = x;
        m_Y = y;
    }

    public void render(Character character) {
        ImGui.setNextWindowPos(m_X, m_Y);
        ImGui.setNextWindowSize(m_Texture.getWidth(), m_Texture.getHeight());

        ImGui.begin("Inventory", ImGuiWindowFlags.NoDecoration);
        ImGui.image(m_Texture.getTextureId(), ImGui.getContentRegionAvail());
        ImGui.end();
    }

}
