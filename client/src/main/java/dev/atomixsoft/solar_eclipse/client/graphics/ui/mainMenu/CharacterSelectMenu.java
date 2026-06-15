package dev.atomixsoft.solar_eclipse.client.graphics.ui.mainMenu;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;
import dev.atomixsoft.solar_eclipse.client.ClientThread;
import dev.atomixsoft.solar_eclipse.client.graphics.Texture;
import dev.atomixsoft.solar_eclipse.client.util.ImGuiFonts;
import dev.atomixsoft.solar_eclipse.core.event.types.SendPacketEvent;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.CharacterSelectReq;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.CharacterListResponse;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.CharacterSummaryPacket;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

import java.util.ArrayList;
import java.util.List;

public class CharacterSelectMenu {

    private final List<CharacterSummaryPacket> m_Characters;

    private boolean m_CreateRequest, m_BackRequest;

    private int m_SelectedSlot;

    public CharacterSelectMenu() {
        m_Characters = new ArrayList<>();

        m_CreateRequest = false;
        m_BackRequest = false;

        m_SelectedSlot = 0;
    }

    public void update(CharacterListResponse packet) {
        m_Characters.clear();
        m_Characters.addAll(packet.characters());

        if(m_Characters.isEmpty())
            m_SelectedSlot = 0;
        else
            m_SelectedSlot = m_Characters.getFirst().slot();
    }

    public void render() {
        m_CreateRequest = false;
        m_BackRequest = false;

        Texture main_menu = AssetLoader.GetTexture("ui_menu_login");
        ImGui.image(main_menu.getTextureId(), main_menu.getWidth(), main_menu.getHeight());

        ImGui.pushStyleColor(ImGuiCol.Border, 1, 1, 1, 0);
        ImGui.pushStyleColor(ImGuiCol.WindowBg, 1, 1, 1, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 0, 0);

        ImGui.setNextWindowPos(38, 12);
        ImGui.setNextWindowSize(main_menu.getWidth(), main_menu.getHeight());

        ImGui.begin("Character_Select",
                ImGuiWindowFlags.NoDecoration |
                        ImGuiWindowFlags.NoBackground |
                        ImGuiWindowFlags.NoMove |
                        ImGuiWindowFlags.NoResize |
                        ImGuiWindowFlags.NoScrollbar |
                        ImGuiWindowFlags.NoSavedSettings);
        ImGui.pushFont(ImGuiFonts.GetFont("georgiab"));

        ImGui.setCursorPos(main_menu.getWidth() / 2.5f, 40);
        ImGui.text("Select Character");

        float listX = 135.0f;
        float listY = 88.0f;
        float rowH = 20.0f;

        if(m_Characters.isEmpty()) {
            ImGui.setCursorPos(listX, listY);
            ImGui.text("No characters found.");
        } else {
            for(int i = 0; i < m_Characters.size(); i++) {
                CharacterSummaryPacket character = m_Characters.get(i);

                boolean selected = character.slot() == m_SelectedSlot;
                String text = (selected ? "> " : "  ") + character.name() + " Level " + character.level();

                ImGui.setCursorPos(listX, listY + i * rowH);
                ImGui.text(text);

                if(ImGui.isItemClicked())
                    m_SelectedSlot = character.slot();
            }
        }

        ImGui.setCursorPos(100, 175);

        if(ImGui.button("Play", 70, 24))
            ClientThread.eventBus().post(new SendPacketEvent(new CharacterSelectReq(m_SelectedSlot)));

        ImGui.sameLine();

        if(ImGui.button("Create", 80, 24))
            m_CreateRequest = true;

        ImGui.sameLine();

        if(ImGui.button("Back", 90, 24))
            m_BackRequest = true;

        ImGui.popFont();
        ImGui.end();

        ImGui.popStyleColor(2);
        ImGui.popStyleVar(4);
    }

    private boolean slotExists(int slot) {
        for(CharacterSummaryPacket character : m_Characters) {
            if(character.slot() == slot)
                return true;
        }

        return false;
    }

    public int nextOpenSlot() {
        final int maxSlots = 3;

        for(int slot = 0; slot < maxSlots; slot++) {
            if(!slotExists(slot))
                return slot;
        }

        return -1;
    }

    public boolean isCreateRequested() {
        return m_CreateRequest;
    }

    public boolean isBackRequested() {
        return m_BackRequest;
    }

}
