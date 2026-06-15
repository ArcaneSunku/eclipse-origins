package dev.atomixsoft.solar_eclipse.client.graphics.ui.mainMenu;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;
import dev.atomixsoft.solar_eclipse.client.ClientThread;
import dev.atomixsoft.solar_eclipse.client.graphics.Texture;
import dev.atomixsoft.solar_eclipse.client.util.ImGuiFonts;
import dev.atomixsoft.solar_eclipse.core.event.types.SendPacketEvent;
import dev.atomixsoft.solar_eclipse.core.game.Constants;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.CreateCharacterRequest;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

public class CharacterCreateMenu {

    private static final float FORM_X = 115.0f;
    private static final float FORM_Y = 82.0f;

    private static final float LABEL_W = 44.0f;
    private static final float INPUT_W = 140.0f;

    private static final float PREVIEW_AREA_X = 315.0f;
    private static final float PREVIEW_AREA_Y = 92.0f;
    private static final float PREVIEW_AREA_W = 90.0f;
    private static final float PREVIEW_AREA_H = 78.0f;

    private static final float CHANGE_SPRITE_W = 120.0f;
    private static final float CHANGE_SPRITE_H = 22.0f;

    private static final float BUTTON_Y = 195.0f;

    private final ImString m_Name;

    private boolean m_BackRequest;

    private int m_Slot;
    private int m_ClassId;
    private byte m_Sex;
    private int m_SpriteId;

    public CharacterCreateMenu() {
        m_Name = new ImString(32);

        m_BackRequest = false;

        m_Slot = 0;
        m_ClassId = 1;
        m_Sex = Constants.SEX_MALE;
        m_SpriteId = 1;
    }

    public void open(int slot) {
        m_Slot = slot;
        m_Name.clear();

        m_BackRequest = false;

        m_ClassId = 1;
        m_Sex = Constants.SEX_MALE;
        m_SpriteId = 1;
    }

    public void render() {
        m_BackRequest = false;

        Texture main_menu = AssetLoader.GetTexture("ui_menu_character");
        ImGui.image(main_menu.getTextureId(), main_menu.getWidth(), main_menu.getHeight());

        ImGui.pushStyleColor(ImGuiCol.Border, 1, 1, 1, 0);
        ImGui.pushStyleColor(ImGuiCol.WindowBg, 1, 1, 1, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 0, 0);

        ImGui.setNextWindowPos(38, 12);
        ImGui.setNextWindowSize(main_menu.getWidth(), main_menu.getHeight());

        ImGui.begin("Character_Create",
                ImGuiWindowFlags.NoDecoration |
                        ImGuiWindowFlags.NoBackground |
                        ImGuiWindowFlags.NoMove |
                        ImGuiWindowFlags.NoResize |
                        ImGuiWindowFlags.NoScrollbar |
                        ImGuiWindowFlags.NoSavedSettings);
        ImGui.pushFont(ImGuiFonts.GetFont("georgiab"));

        ImGui.setCursorPos(FORM_X, FORM_Y);
        ImGui.text("Name:");
        ImGui.sameLine();

        ImGui.setCursorPosX(FORM_X + LABEL_W);
        ImGui.pushItemWidth(INPUT_W);
        ImGui.inputText("##newCharacterName", m_Name);
        ImGui.popItemWidth();

        ImGui.setCursorPos(FORM_X, FORM_Y + 28);
        ImGui.text("Class: ");
        ImGui.sameLine();
        ImGui.setCursorPosX(FORM_X + LABEL_W);

        if(ImGui.button(className(), 100, 20)) {
            m_ClassId++;

            if(m_ClassId > 2)
                m_ClassId = 1;

            updateSpriteFromSexAndClass();
        }

        ImGui.setCursorPos(FORM_X, FORM_Y + 56);
        ImGui.text("Gender:");
        ImGui.sameLine();

        if(ImGui.radioButton("Male", m_Sex == Constants.SEX_MALE)) {
            m_Sex = Constants.SEX_MALE;
            updateSpriteFromSexAndClass();
        }

        ImGui.sameLine();

        if(ImGui.radioButton("Female", m_Sex == Constants.SEX_FEMALE)) {
            m_Sex = Constants.SEX_FEMALE;
            updateSpriteFromSexAndClass();
        }

        Texture sprite = AssetLoader.GetTexture("character" + m_SpriteId);

        if(sprite != null) {
            float previewW = sprite.getWidth() / 4.0f;
            float previewH = sprite.getHeight() / 4.0f;

            float spriteX = PREVIEW_AREA_X + (PREVIEW_AREA_W - previewW) * 0.5f;
            float spriteY = PREVIEW_AREA_Y + (PREVIEW_AREA_H - previewH) * 0.5f;

            ImGui.setCursorPos(spriteX, spriteY);

            ImGui.image(
                    sprite.getTextureId(),
                    previewW,
                    previewH,
                    0,
                    0,
                    0.25f,
                    0.25f
            );
        }

        float changeSpriteX = PREVIEW_AREA_X + (PREVIEW_AREA_W - CHANGE_SPRITE_W) * 0.5f;
        float changeSpriteY = PREVIEW_AREA_Y + PREVIEW_AREA_H + 4.0f;

        ImGui.setCursorPos(changeSpriteX, changeSpriteY);

        if(ImGui.button("Change Sprite", CHANGE_SPRITE_W, CHANGE_SPRITE_H)) {
            cycleSprite();
        }

        ImGui.setCursorPos(125, BUTTON_Y);

        if(ImGui.button("Accept", 80, 22)) {
            sendCreateCharacter();
        }

        ImGui.sameLine();

        if(ImGui.button("Back", 80, 22)) {
            m_BackRequest = true;
        }

        ImGui.popFont();
        ImGui.end();

        ImGui.popStyleColor(2);
        ImGui.popStyleVar(4);
    }

    public boolean isBackRequested() {
        return m_BackRequest;
    }

    private void sendCreateCharacter() {
        String name = m_Name.get().trim();

        if(name.isBlank())
            return;

        if(m_Slot < 0)
            return;

        ClientThread.eventBus().post(new SendPacketEvent(new CreateCharacterRequest(m_Slot, name, m_ClassId, m_Sex, m_SpriteId)));
    }

    private String className() {
        return switch(m_ClassId) {
            case 1 -> "Warrior";
            case 2 -> "Magician";
            default -> "Unknown";
        };
    }

    private void updateSpriteFromSexAndClass() {
        if(m_Sex == Constants.SEX_MALE)
            m_SpriteId = 1;
        else
            m_SpriteId = 2;
    }

    private void cycleSprite() {
        if(m_ClassId == 2 && m_Sex == Constants.SEX_FEMALE) {
            m_SpriteId++;

            if(m_SpriteId > 3)
                m_SpriteId = 2;

            return;
        }

        updateSpriteFromSexAndClass();
    }

}
