package dev.atomixsoft.solar_eclipse.client.graphics.ui.ingame;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;
import dev.atomixsoft.solar_eclipse.client.game.ClientPlayerStats;
import dev.atomixsoft.solar_eclipse.client.graphics.Texture;
import dev.atomixsoft.solar_eclipse.client.util.ImGuiFonts;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

public class PlayerHud {

    private static final float HEALTH_X = 518.0f, HEALTH_Y = 28.0f;
    private static final float SPIRIT_X = 518.0f, SPIRIT_Y = 50.0f;
    private static final float EXP_X = 518.0f, EXP_Y = 72.0f;

    private static final float GOLD_X = 568.0f, GOLD_Y = 102.0f;
    private static final float PING_X = 568.0f, PING_Y = 129.0f;

    private final Texture m_HealthBar;
    private final Texture m_SpiritBar;
    private final Texture m_ExpBar;

    public PlayerHud() {
        m_HealthBar = AssetLoader.GetTexture("ui_main_health_bar");
        m_SpiritBar = AssetLoader.GetTexture("ui_main_spirit_bar");
        m_ExpBar = AssetLoader.GetTexture("ui_main_exp_bar");
    }

    public void render(ClientPlayerStats stats) {
        if(stats == null)
            return;

        ImGui.pushStyleColor(ImGuiCol.Border, 1, 1, 1, 0);
        ImGui.pushStyleColor(ImGuiCol.WindowBg, 1, 1, 1, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 0, 0);

        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(785, 594);

        ImGui.begin("Player_HUD", ImGuiWindowFlags.NoDecoration |
                ImGuiWindowFlags.NoBackground |
                ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoScrollbar |
                ImGuiWindowFlags.NoSavedSettings |
                ImGuiWindowFlags.NoInputs);

        drawBar(m_HealthBar, HEALTH_X, HEALTH_Y, ratio(stats.health, stats.maxHealth));
        drawBar(m_SpiritBar, SPIRIT_X, SPIRIT_Y, ratio(stats.spirit, stats.maxSpirit));
        drawBar(m_ExpBar, EXP_X, EXP_Y, ratio(stats.experience,  stats.maxExperience));

        drawText(GOLD_X, GOLD_Y, String.valueOf(stats.gold));
        drawText(PING_X, PING_Y, stats.ping + "ms");

        ImGui.end();

        ImGui.popStyleColor(2);
        ImGui.popStyleVar(4);
    }

    private void drawBar(Texture texture, float x, float y, float ratio) {
        if(texture == null)
            return;

        ratio = clampNormalized(ratio);
        float visibleWidth = texture.getWidth() * ratio;

        if(visibleWidth <= 0.0f)
            return;

        float u2 = visibleWidth / texture.getWidth();

        ImGui.setCursorPos(x, y);
        ImGui.image(texture.getTextureId(), visibleWidth, texture.getHeight(), 0.0f, 0.0f, u2, 1.0f);
    }

    private void drawText(float x, float y, String text) {
        ImGui.setCursorPos(x, y);

        ImGui.pushFont(ImGuiFonts.GetFont("georgiab"));
        ImGui.text(text);
        ImGui.popFont();
    }

    private float ratio(int value, int max) {
        if(max <= 0)
            return 0.0f;

        return (float) value / (float) max;
    }

    private float clampNormalized(float value) {
        if(value < 0.0f)
            return 0.0f;

        return Math.min(value, 1.0f);
    }

}
