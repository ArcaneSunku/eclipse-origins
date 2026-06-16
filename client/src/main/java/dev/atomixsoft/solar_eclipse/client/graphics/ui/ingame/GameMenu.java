package dev.atomixsoft.solar_eclipse.client.graphics.ui.ingame;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;
import dev.atomixsoft.solar_eclipse.client.game.ClientInventory;
import dev.atomixsoft.solar_eclipse.client.game.ClientItemDefinitions;
import dev.atomixsoft.solar_eclipse.client.graphics.Texture;
import dev.atomixsoft.solar_eclipse.client.graphics.ui.Button;
import dev.atomixsoft.solar_eclipse.core.game.character.CharacterData;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

import java.util.LinkedHashMap;
import java.util.Map;

public class GameMenu {

    public enum GameMenuState {
        Inventory, Skills, Character,
        Options, Trading, Party
    }

    private final Map<String, Texture> m_MenuTextures;
    private final Map<String, Button> m_Buttons;

    private Hotbar m_Hotbar;
    private InventoryMenu m_Inventory;

    private GameMenuState m_State;
    private CharacterData m_Player;

    public GameMenu() {
        m_MenuTextures = new LinkedHashMap<>();
        m_Buttons = new LinkedHashMap<>();

        gatherTextures();
        setup();
    }

    private void gatherTextures() {
        m_MenuTextures.put("hotbar", AssetLoader.GetTexture("ui_main_hotbar"));
        m_MenuTextures.put("inventory", AssetLoader.GetTexture("ui_main_inventory"));
        m_MenuTextures.put("skills", AssetLoader.GetTexture("ui_main_skills"));
        m_MenuTextures.put("character", AssetLoader.GetTexture("ui_main_character"));
        m_MenuTextures.put("options", AssetLoader.GetTexture("ui_main_options"));
        m_MenuTextures.put("party", AssetLoader.GetTexture("ui_main_party"));
        m_MenuTextures.put("item_tooltip", AssetLoader.GetTexture("ui_main_itemDesc"));
        m_MenuTextures.put("spell_tooltip", AssetLoader.GetTexture("ui_main_spellDesc"));
    }

    public void setup() {
        m_State = GameMenuState.Inventory;

        m_Buttons.put("btn_inv", new Button("btn_main_inv"));
        m_Buttons.put("btn_skills", new Button("btn_main_skills"));
        m_Buttons.put("btn_char", new Button("btn_main_char"));
        m_Buttons.put("btn_opt", new Button("btn_main_opt"));
        m_Buttons.put("btn_party", new Button("btn_main_party"));
        m_Buttons.put("btn_trade", new Button("btn_main_trade"));

        if(m_Hotbar == null) m_Hotbar = new Hotbar(m_MenuTextures.get("hotbar"), 12, 399);
        if(m_Inventory == null) m_Inventory = new InventoryMenu(m_MenuTextures.get("inventory"), m_MenuTextures.get("item_tooltip"), 541, 283);
    }

    public void render(CharacterData player, ClientInventory inventory, ClientItemDefinitions items) {
        m_Player = player;
        if(m_Player == null)
            return;

        renderMenu(inventory, items);
        renderButtons();

        m_Hotbar.render();
    }

    private void renderButtons() {
        ImGui.setNextWindowPos(518, 185);
        ImGui.setNextWindowSize(239, 79);

        ImGui.pushStyleColor(ImGuiCol.Border, 1, 1, 1, 0);
        ImGui.pushStyleColor(ImGuiCol.WindowBg, 1, 1, 1, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 0, 0);

        ImGui.begin("Menu_Buttons", ImGuiWindowFlags.NoDecoration);

        Button inv_button = m_Buttons.get("btn_inv");
        if(inv_button != null) {
            inv_button.render("Inventory_Button", 5, 5);

            if(inv_button.getState() == Button.State.CLICKED)
                m_State = GameMenuState.Inventory;
        }

        Button skills_button = m_Buttons.get("btn_skills");
        if(skills_button != null) {
            skills_button.render("Skills_Button", 16 + skills_button.getWidth(), 5);

            if(skills_button.getState() == Button.State.CLICKED)
                m_State = GameMenuState.Skills;
        }

        Button char_button = m_Buttons.get("btn_char");
        if(char_button != null) {
            char_button.render("Character_Button", 16 + char_button.getWidth() * 2 + 11, 5);

            if(char_button.getState() == Button.State.CLICKED)
                m_State = GameMenuState.Character;
        }

        Button opt_btn = m_Buttons.get("btn_opt");
        if(opt_btn != null) {
            opt_btn.render("Option_Button", 5, 16 + opt_btn.getHeight());

            if(opt_btn.getState() == Button.State.CLICKED)
                m_State = GameMenuState.Options;
        }

        Button trade_btn = m_Buttons.get("btn_trade");
        if(trade_btn != null) {
            trade_btn.render("Trade_Button", 16 + trade_btn.getWidth(), 16 + trade_btn.getHeight());

//            if(trade_btn.getState() == Button.State.CLICKED)
//                m_State = MenuState.Options;
        }

        Button party_btn = m_Buttons.get("btn_party");
        if(party_btn != null) {
            party_btn.render("Party_Button", 16 + party_btn.getWidth() * 2 + 11, 16 + party_btn.getHeight());

            if(party_btn.getState() == Button.State.CLICKED)
                m_State = GameMenuState.Party;
        }
        ImGui.end();

        ImGui.popStyleColor(2);
        ImGui.popStyleVar(4);
    }

    private void renderMenu(ClientInventory inventory, ClientItemDefinitions items) {
        ImGui.setNextWindowPos(541, 283);

        switch(m_State) {
            case Inventory -> m_Inventory.render(inventory, items);

            case Skills -> {
                Texture skills = m_MenuTextures.get("skills");
                ImGui.setNextWindowSize(skills.getWidth(), skills.getHeight());

                ImGui.begin("Skills", ImGuiWindowFlags.NoDecoration);
                ImGui.image(skills.getTextureId(), ImGui.getContentRegionAvail());
                ImGui.end();
            }

            case Character -> {
                Texture character = m_MenuTextures.get("character");
                ImGui.setNextWindowSize(character.getWidth(), character.getHeight());

                ImGui.begin("Character", ImGuiWindowFlags.NoDecoration);
                ImGui.image(character.getTextureId(), ImGui.getContentRegionAvail());
                ImGui.end();
            }

            case Options -> {
                Texture options = m_MenuTextures.get("options");
                ImGui.setNextWindowSize(options.getWidth(), options.getHeight());

                ImGui.begin("Options", ImGuiWindowFlags.NoDecoration);
                ImGui.image(options.getTextureId(), ImGui.getContentRegionAvail());
                ImGui.end();
            }

            case Trading -> {
                // TODO: Implement trading menu
            }

            case Party -> {
                Texture party = m_MenuTextures.get("party");
                ImGui.setNextWindowSize(party.getWidth(), party.getHeight());

                ImGui.begin("Party", ImGuiWindowFlags.NoDecoration);
                ImGui.image(party.getTextureId(), ImGui.getContentRegionAvail());
                ImGui.end();
            }
        }
    }

    public GameMenuState getState() {
        return m_State;
    }

    public Hotbar getHotbar() {
        return m_Hotbar;
    }

}
