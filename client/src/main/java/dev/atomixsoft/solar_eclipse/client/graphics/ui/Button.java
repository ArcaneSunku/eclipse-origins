package dev.atomixsoft.solar_eclipse.client.graphics.ui;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;
import dev.atomixsoft.solar_eclipse.client.graphics.Texture;

import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class Button {

    public static enum State {
        NORMAL,
        HOVERED,
        CLICKED
    }

    private final List<Texture> m_Textures;
    private State m_State;

    /**
     * <p>Creates a button from a list of {@link Texture}s relative to the button's state.</br>
     * i.e. button, button_hover, button_click
     * </p>
     * @param textures a list of textures containing the normal, hover, and clicked states
     */
    public Button(List<Texture> textures) {
        m_Textures = textures;
        m_State = State.NORMAL;
    }

    public Button(String texture_name) {
        m_Textures = new ArrayList<>();

        m_Textures.add(AssetLoader.GetTexture(texture_name));
        m_Textures.add(AssetLoader.GetTexture(texture_name + "_hover"));
        m_Textures.add(AssetLoader.GetTexture(texture_name + "_click"));

        m_State = State.NORMAL;
    }

    public void render(String id, int x, int y) {
        int flag = -1;
        switch (m_State) {
            case NORMAL -> flag = 0;
            case HOVERED -> flag = 1;
            case CLICKED -> flag = 2;
        }

        Texture button = m_Textures.get(flag);

        ImGui.setCursorPos(x, y);
        if(ImGui.imageButton(id, button.getTextureId(), button.getWidth(), button.getHeight())) {
            m_State = State.CLICKED;
        } else {
            if(ImGui.isItemHovered()) m_State = State.HOVERED;
            else if(!ImGui.isItemHovered()) m_State = State.NORMAL;
        }
    }

    public int getWidth() {
        int flag = -1;
        switch (m_State) {
            case NORMAL -> flag = 0;
            case HOVERED -> flag = 1;
            case CLICKED -> flag = 2;
        }

        return m_Textures.get(flag).getWidth();
    }

    public int getHeight() {
        int flag = -1;
        switch (m_State) {
            case NORMAL -> flag = 0;
            case HOVERED -> flag = 1;
            case CLICKED -> flag = 2;
        }

        return m_Textures.get(flag).getHeight();
    }

    public State getState() {
        return m_State;
    }

}
