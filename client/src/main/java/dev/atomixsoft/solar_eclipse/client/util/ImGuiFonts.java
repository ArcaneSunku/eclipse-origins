package dev.atomixsoft.solar_eclipse.client.util;

import imgui.ImFont;
import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImGuiIO;

import java.util.HashMap;
import java.util.Map;

public class ImGuiFonts {
    private static ImGuiFonts INSTANCE = null;

    private final Map<String, ImFont> m_Fonts;

    private ImGuiFonts() {
        m_Fonts = new HashMap<>();
    }

    public static void Initialize() {
        if(INSTANCE == null)
            INSTANCE = new ImGuiFonts();

        ImGuiIO io = ImGui.getIO();
        ImFontAtlas fonts = io.getFonts();

        INSTANCE.m_Fonts.put("georgia", fonts.addFontFromFileTTF("client/assets/fonts/georgia.ttf", 12));
        INSTANCE.m_Fonts.put("georgiab", fonts.addFontFromFileTTF("client/assets/fonts/georgiab.ttf", 12));
    }

    public static ImFont GetFont(String name) {
        return INSTANCE.m_Fonts.get(name.toLowerCase());
    }

}
