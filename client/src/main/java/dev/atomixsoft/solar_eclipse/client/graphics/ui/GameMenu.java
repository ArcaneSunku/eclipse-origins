package dev.atomixsoft.solar_eclipse.client.graphics.ui;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;
import dev.atomixsoft.solar_eclipse.client.graphics.GameRenderer;
import dev.atomixsoft.solar_eclipse.client.graphics.Texture;
import dev.atomixsoft.solar_eclipse.core.game.character.Character;
import dev.atomixsoft.solar_eclipse.core.game.map.GameMap;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameMenu {

    public enum MenuState {
        Inventory, Skills, Character,
        Options, Trading, Party
    }

    private Hotbar m_Hotbar;
    private InventoryMenu m_Inventory;

    private MenuState m_State;
    private Character m_Player;

    private int m_InvFlag, m_SkillsFlag, m_CharFlag;
    private int m_OptFlag, m_TradeFlag, m_PartyFlag;

    private final Map<String, Texture> m_MenuTextures;
    private final Map<String, List<Texture>> m_ButtonTextures;

    public GameMenu() {
        m_MenuTextures = new LinkedHashMap<>();
        m_ButtonTextures = new LinkedHashMap<>();

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

        List<Texture> buttons = new ArrayList<>();
        buttons.add(AssetLoader.GetTexture("btn_main_inv"));
        buttons.add(AssetLoader.GetTexture("btn_main_inv_hover"));
        buttons.add(AssetLoader.GetTexture("btn_main_inv_click"));
        m_ButtonTextures.put("btn_inv", buttons);

        buttons = new ArrayList<>();
        buttons.add(AssetLoader.GetTexture("btn_main_skills"));
        buttons.add(AssetLoader.GetTexture("btn_main_skills_hover"));
        buttons.add(AssetLoader.GetTexture("btn_main_skills_click"));
        m_ButtonTextures.put("btn_skills", buttons);


        buttons = new ArrayList<>();
        buttons.add(AssetLoader.GetTexture("btn_main_char"));
        buttons.add(AssetLoader.GetTexture("btn_main_char_hover"));
        buttons.add(AssetLoader.GetTexture("btn_main_char_click"));
        m_ButtonTextures.put("btn_char", buttons);


        buttons = new ArrayList<>();
        buttons.add(AssetLoader.GetTexture("btn_main_opt"));
        buttons.add(AssetLoader.GetTexture("btn_main_opt_hover"));
        buttons.add(AssetLoader.GetTexture("btn_main_opt_click"));
        m_ButtonTextures.put("btn_opt", buttons);


        buttons = new ArrayList<>();
        buttons.add(AssetLoader.GetTexture("btn_main_party"));
        buttons.add(AssetLoader.GetTexture("btn_main_party_hover"));
        buttons.add(AssetLoader.GetTexture("btn_main_party_click"));
        m_ButtonTextures.put("btn_party", buttons);


        buttons = new ArrayList<>();
        buttons.add(AssetLoader.GetTexture("btn_main_trade"));
        buttons.add(AssetLoader.GetTexture("btn_main_trade_hover"));
        buttons.add(AssetLoader.GetTexture("btn_main_trade_click"));
        m_ButtonTextures.put("btn_trade", buttons);
    }

    public void setup() {
        m_State = MenuState.Inventory;

        m_InvFlag = m_SkillsFlag = m_CharFlag = 0;
        m_OptFlag = m_TradeFlag = m_PartyFlag = 0;

        if(m_Hotbar == null) m_Hotbar = new Hotbar(m_MenuTextures.get("hotbar"), 12, 399);
        if(m_Inventory == null) m_Inventory = new InventoryMenu(m_MenuTextures.get("inventory"), 541, 283);
    }

    public void render(GameRenderer renderer) {
        if(m_Player == null) {
            GameMap map = renderer.getMap();
            for(Character ch : map.MapCharacters) {
                if(ch.player) {
                    m_Player = ch;
                    break;
                }
            }
        }

        renderButtons();
        renderMenu();

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
        Texture inv_btn = m_ButtonTextures.get("btn_inv").get(m_InvFlag);
        ImGui.setCursorPos(5, 5);

        if(ImGui.imageButton("Inventory_Button", inv_btn.getTextureId(), inv_btn.getWidth(), inv_btn.getHeight())) {
            m_State = MenuState.Inventory;
            m_InvFlag = 2;
        } else {
            if(ImGui.isItemHovered()) m_InvFlag = 1;
            else if(!ImGui.isItemHovered()) m_InvFlag = 0;
        }

        Texture skills_btn = m_ButtonTextures.get("btn_skills").get(m_SkillsFlag);
        ImGui.setCursorPos(16 + skills_btn.getWidth(), 5);

        if(ImGui.imageButton("Skills_Button", skills_btn.getTextureId(), skills_btn.getWidth(), skills_btn.getHeight())) {
            m_State = MenuState.Skills;
            m_SkillsFlag = 2;
        } else {
            if(ImGui.isItemHovered()) m_SkillsFlag = 1;
            else if(!ImGui.isItemHovered()) m_SkillsFlag = 0;
        }

        Texture char_button = m_ButtonTextures.get("btn_char").get(m_CharFlag);
        ImGui.setCursorPos(16 + skills_btn.getWidth() * 2 + 11, 5);

        if(ImGui.imageButton("Character_Button", char_button.getTextureId(), char_button.getWidth(), char_button.getHeight())) {
            m_State = MenuState.Character;
            m_CharFlag = 2;
        } else {
            if(ImGui.isItemHovered()) m_CharFlag = 1;
            else if(!ImGui.isItemHovered()) m_CharFlag = 0;
        }

        Texture opt_btn = m_ButtonTextures.get("btn_opt").get(m_OptFlag);
        ImGui.setCursorPos(5, 16 + opt_btn.getHeight());

        if(ImGui.imageButton("Option_Button", opt_btn.getTextureId(), opt_btn.getWidth(), opt_btn.getHeight())) {
            m_State = MenuState.Options;
            m_OptFlag = 2;
        } else {
            if(ImGui.isItemHovered()) m_OptFlag = 1;
            else if(!ImGui.isItemHovered()) m_OptFlag = 0;
        }

        Texture trade_btn = m_ButtonTextures.get("btn_trade").get(m_TradeFlag);
        ImGui.setCursorPos(16 + trade_btn.getWidth(), 16 + trade_btn.getHeight());

        if(ImGui.imageButton("Trade_Button", trade_btn.getTextureId(), trade_btn.getWidth(), trade_btn.getHeight())) {
//            m_State = MenuState.Options;
            m_TradeFlag = 2;
        } else {
            if(ImGui.isItemHovered()) m_TradeFlag = 1;
            else if(!ImGui.isItemHovered()) m_TradeFlag = 0;
        }

        Texture party_btn = m_ButtonTextures.get("btn_party").get(m_PartyFlag);
        ImGui.setCursorPos(16 + party_btn.getWidth() * 2 + 11, 16 + party_btn.getHeight());

        if(ImGui.imageButton("Party_Button", party_btn.getTextureId(), party_btn.getWidth(), party_btn.getHeight())) {
            m_State = MenuState.Party;
            m_PartyFlag = 2;
        } else {
            if(ImGui.isItemHovered()) m_PartyFlag = 1;
            else if(!ImGui.isItemHovered()) m_PartyFlag = 0;
        }
        ImGui.end();

        ImGui.popStyleColor(2);
        ImGui.popStyleVar(4);
    }

    private void renderMenu() {
        ImGui.setNextWindowPos(541, 283);

        switch(m_State) {
            case Inventory -> m_Inventory.render(m_Player);

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

    public MenuState getState() {
        return m_State;
    }

    public Hotbar getHotbar() {
        return m_Hotbar;
    }

}
