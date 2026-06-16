package dev.atomixsoft.solar_eclipse.client.graphics.ui.ingame;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;
import dev.atomixsoft.solar_eclipse.client.game.ClientInventory;
import dev.atomixsoft.solar_eclipse.client.game.ClientItemDefinitions;
import dev.atomixsoft.solar_eclipse.client.graphics.Texture;
import dev.atomixsoft.solar_eclipse.client.util.ImGuiFonts;
import dev.atomixsoft.solar_eclipse.core.game.Constants;
import dev.atomixsoft.solar_eclipse.core.net.data.InventorySlotData;
import dev.atomixsoft.solar_eclipse.core.net.data.ItemDefinitionData;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class InventoryMenu {

    private static final int COLUMNS = 5;

    private final Texture m_MenuUI;
    private final Texture m_TooltipUI;

    private final int m_X, m_Y;

    public InventoryMenu(Texture menuUI, Texture tooltipUI, int x, int y) {
        m_MenuUI = menuUI;
        m_TooltipUI = tooltipUI;

        m_X = x;
        m_Y = y;
    }

    public void render(ClientInventory inventory, ClientItemDefinitions items) {
        ImGui.setNextWindowPos(m_X, m_Y);
        ImGui.setNextWindowSize(m_MenuUI.getWidth(), m_MenuUI.getHeight());

        ImGui.pushFont(ImGuiFonts.GetFont("georgiab"));
        ImGui.begin("Inventory", ImGuiWindowFlags.NoDecoration);
        ImGui.image(m_MenuUI.getTextureId(), ImGui.getContentRegionAvail());
        drawSlots(inventory, items);
        ImGui.end();
        ImGui.popFont();
    }

    private void drawSlots(ClientInventory inventory, ClientItemDefinitions items) {
        if(inventory == null)
            return;

        for(int slot = 0; slot < Constants.MAX_INV; slot++ ) {
            InventorySlotData data = inventory.getSlot(slot);

            int column = slot % COLUMNS;
            int row = slot / COLUMNS;

            float x = 20 + column * 32;
            float y = 34 + row * 32;

            ImGui.setCursorPos(x, y);

            if(data == null || data.itemId() <= 0) {
                ImGui.invisibleButton("##inv_slot_" + slot, 28, 28);
                continue;
            }

            ItemDefinitionData item = items != null ? items.get(data.itemId()) : null;
            String label = item != null ? item.name() : String.valueOf(data.itemId());

            if(item != null) {
                Texture itemTexture = AssetLoader.GetTexture("item" + item.iconId());

                if(itemTexture != null) {
                    ImGui.imageButton(
                            "##inv_slot_" + slot,
                            itemTexture.getTextureId(),
                            28,
                            28,
                            0.5f, 0.0f,
                            1.0f, 1.0f
                    );
                } else {
                    ImGui.button(label + "##inv_slot_" + slot, 28, 28);
                }
            }

            if(ImGui.isItemHovered() && item != null)
                renderItemTooltip(item, data.amount());

            if(data.amount() > 1) {
                ImGui.setCursorPos(x + 14, y + 15);
                ImGui.text(String.valueOf(data.amount()));
            }
        }
    }

    private void renderItemTooltip(ItemDefinitionData item, int amount) {
        if(item == null)
            return;

        float tooltipW = m_TooltipUI.getWidth();
        float tooltipH = m_TooltipUI.getHeight();

        float mouseX = ImGui.getMousePos().x;
        float mouseY = ImGui.getMousePos().y;

        float viewportW = ImGui.getMainViewport().getSizeX();
        float viewportH = ImGui.getMainViewport().getSizeY();

        float x = mouseX + 16;
        float y = mouseY + 16;

        if(x + tooltipW > viewportW)
            x = mouseX - tooltipW - 16;

        if(y + tooltipH > viewportH)
            y = mouseY - tooltipH - 16;

        if(x < 0) x = 0;
        if(y < 0) y = 0;

        ImGui.setNextWindowPos(x, y);
        ImGui.setNextWindowSize(tooltipW, tooltipH);

        ImGui.begin(
                "##item_tooltip",
                ImGuiWindowFlags.NoDecoration |
                        ImGuiWindowFlags.NoInputs |
                        ImGuiWindowFlags.NoSavedSettings |
                        ImGuiWindowFlags.Tooltip|
                        ImGuiWindowFlags.NoBackground
        );

        ImGui.image(
                m_TooltipUI.getTextureId(),
                tooltipW,
                tooltipH
        );

        // Name area
        float nameAreaX = 12.0f;
        float nameAreaY = 14.0f;
        float nameAreaW = tooltipW - 24.0f;

        float nameW = ImGui.calcTextSize(item.name()).x;
        float nameX = nameAreaX + (nameAreaW - nameW) / 2.0f;

        ImGui.setCursorPos(nameX, nameAreaY);
        ImGui.text(item.name());

        // Icon area
        float iconSize = 64.0f;
        float iconX = (tooltipW - iconSize) / 2.0f;
        float iconY = 40.0f;

        Texture itemTexture = AssetLoader.GetTexture("item" + item.iconId());

        ImGui.setCursorPos(iconX, iconY);
        ImGui.image(itemTexture.getTextureId(),
                iconSize, iconSize, 0.5f, 0.0f, 1.0f, 1.0f);

        drawDescription(item.description());

        ImGui.end();
    }

    private void drawDescription(String description) {
        float boxX = 12.0f;
        float boxY = 117.0f;
        float boxW = 185.0f;
        float boxH = 110.0f;

        float padX = 4.0f;
        float padY = 4.0f;

        float textX = boxX + padX;
        float textY = boxY + padY;
        float textW = boxW - padX * 2.0f;
        float textH = boxH - padY * 2.0f;

        ImGui.setCursorPos(textX, textY);

        ImGui.beginChild(
                "##item_description_box",
                textW,
                textH,
                false,
                ImGuiWindowFlags.NoScrollbar |
                        ImGuiWindowFlags.NoScrollWithMouse |
                        ImGuiWindowFlags.NoBackground
        );

        ImGui.pushTextWrapPos(textW);
        ImGui.textWrapped(description);
        ImGui.popTextWrapPos();

        ImGui.endChild();
    }

}
