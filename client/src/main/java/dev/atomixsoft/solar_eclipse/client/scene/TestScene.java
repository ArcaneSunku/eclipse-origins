package dev.atomixsoft.solar_eclipse.client.scene;

import dev.atomixsoft.solar_eclipse.client.ClientThread;
import dev.atomixsoft.solar_eclipse.client.graphics.FrameBuffer;
import dev.atomixsoft.solar_eclipse.client.graphics.GameRenderer;
import dev.atomixsoft.solar_eclipse.client.graphics.Texture;
import dev.atomixsoft.solar_eclipse.core.event.types.ShutdownEvent;
import dev.atomixsoft.solar_eclipse.core.game.Actuator;
import dev.atomixsoft.solar_eclipse.core.game.Item;
import dev.atomixsoft.solar_eclipse.core.game.character.Character;
import dev.atomixsoft.solar_eclipse.core.game.map.GameMap;
import dev.atomixsoft.solar_eclipse.core.game.map.Tile;
import imgui.*;
import imgui.flag.*;
import org.joml.Vector2f;
import org.joml.Vector3f;

import dev.atomixsoft.solar_eclipse.core.game.Constants;

import dev.atomixsoft.solar_eclipse.client.util.input.Controller;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;

import dev.atomixsoft.solar_eclipse.client.graphics.render2D.SpriteBatch;
import dev.atomixsoft.solar_eclipse.client.graphics.cameras.OrthoCamera;

import static dev.atomixsoft.solar_eclipse.core.event.types.InputEvent.InputType;
import static org.lwjgl.opengl.GL11.*;

/**
 * <p>Purely for prototyping features in the earlier stages of development.</p>
 */
public class TestScene extends SceneAdapter {

    private OrthoCamera camera;
    private SpriteBatch batch;
    private FrameBuffer frameBuffer;

    private GameRenderer gameRender;

    @Override
    public void show() {
        camera = new OrthoCamera(476, 380);
        camera.setZoom(16 * 9);

        AssetLoader.AddShader("basic", "basic");
        batch = new SpriteBatch(AssetLoader.GetShader("basic"));

        frameBuffer = new FrameBuffer(476, 380);
        gameRender = new GameRenderer();

        loadNonUITextures("animation", 3);
        loadNonUITextures("character", 3);
        loadNonUITextures("face", 3);
        loadNonUITextures("item", 14);
        loadNonUITextures("tileset", 2);

        loadGUITextures("menu");
        loadGUITextures("main");

        GameMap testMap = new GameMap(0, 0, 12, 10);

        Tile grassTile = new Tile();
        grassTile.textureId = 1;
        grassTile.textureX = 0;
        grassTile.textureY = 1;

        grassTile.type = Constants.TILE_TYPE_WALKABLE;
        grassTile.roof = false;

        Tile trunkTile = new Tile(grassTile);
        trunkTile.textureX = 4;
        trunkTile.textureY = 0;
        trunkTile.type = Constants.TILE_TYPE_BLOCKED;
        trunkTile.roof = false;

        Actuator.FillMapLayer(testMap, grassTile, 0);

        Actuator.AddTileToMap(testMap, trunkTile, 1, 2, 2);
        Actuator.AddTileToMap(testMap, trunkTile, 1, 9, 8);
        Actuator.AddTileToMap(testMap, trunkTile, 1, 9, 9);
        Actuator.AddTileToMap(testMap, trunkTile, 1, 9, 2);
        Actuator.AddTileToMap(testMap, trunkTile, 1, 2, 9);

        Character testChar = new Character();
        testChar.name = "Angel";
        testChar.textureId = 3;
        testChar.keyFrame = 0;
        testChar.facing = Character.Direction.DOWN;
        testChar.player = false;
        testChar.sex = Constants.SEX_OTHER;

        Character testChar2 = new Character();
        testChar2.name = "Jim";
        testChar2.textureId = 1;
        testChar2.keyFrame = 0;
        testChar2.facing = Character.Direction.DOWN;
        testChar2.player = true;
        testChar2.sex = Constants.SEX_MALE;

        Actuator.AddCharacterToMap(testMap, testChar, 0, 0);
        Actuator.AddCharacterToMap(testMap, testChar2, 2, 3);

        gameRender.setMap(testMap);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void update(Controller input, double dt) {
        if(input.isPressed(InputType.CANCEL))
            ClientThread.eventBus().post(new ShutdownEvent("dev", false));

        gameRender.update(camera);
    }

    @Override
    public void render() {
        frameBuffer.bind();
        glViewport(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        batch.begin(camera);
        gameRender.render(batch, camera);
        batch.end();
        frameBuffer.unbind();

        glViewport(0, 0, (int) ClientThread.size().x, (int) ClientThread.size().y);
    }

    @Override
    public void imgui() {
        ImGuiIO io = ImGui.getIO();

        ImGui.pushStyleColor(ImGuiCol.Border, 1, 1, 1, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 0, 0);

        Texture main_bg = AssetLoader.GetTexture("ui_main_main");
        Texture hotbar = AssetLoader.GetTexture("ui_main_hotbar");

        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(main_bg.getWidth(), main_bg.getHeight());

        ImGui.begin("Background", ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoBringToFrontOnFocus);
        // Background Image
        ImGui.image(main_bg.getTextureId(), ImGui.getContentRegionAvail());

        // Game Buffer
        ImGui.setCursorPos(12, 12);
        ImGui.image(frameBuffer.getColorBufferId(), new ImVec2(frameBuffer.getWidth(), frameBuffer.getHeight()), new ImVec2(0, 1), new ImVec2(1, 0));

        // Hotbar Image
        ImGui.setCursorPos(12, 399);
        ImGui.image(hotbar.getTextureId(), hotbar.getWidth(), hotbar.getHeight());

        ImGui.end();

        ImGui.popStyleVar(4);
        ImGui.popStyleColor();
    }

    @Override
    public void dispose() {
        if(batch != null)
            batch.dispose();

        frameBuffer.dispose();
    }

    /**
     * Loads the GUI textures related to the name you pass. </br>
     * This will search for a folder with the given name and load what you specify from there. </br>
     * Calls to {@link #loadButtonTextures(String, String)} should be called in here somewhere.
     *
     * @param name the name of the folder you want to load the UI textures for
     */
    private void loadGUITextures(String name) {
        if(name.equalsIgnoreCase("main")) {
            AssetLoader.AddTexture("ui_" + name + "_bank",       "gui/" + name + "/bank.jpg");
            AssetLoader.AddTexture("ui_" + name + "_character",  "gui/" + name + "/character.jpg");
            AssetLoader.AddTexture("ui_" + name + "_itemDesc",   "gui/" + name + "/description_item.jpg");
            AssetLoader.AddTexture("ui_" + name + "_spellDesc",  "gui/" + name + "/description_spell.jpg");
            AssetLoader.AddTexture("ui_" + name + "_dragbox",    "gui/" + name + "/dragbox.jpg");
            AssetLoader.AddTexture("ui_" + name + "_hotbar",     "gui/" + name + "/hotbar.jpg");
            AssetLoader.AddTexture("ui_" + name + "_inventory",  "gui/" + name + "/inventory.jpg");
            AssetLoader.AddTexture("ui_" + name + "_main",       "gui/" + name + "/main.jpg");
            AssetLoader.AddTexture("ui_" + name + "_options",    "gui/" + name + "/options.jpg");
            AssetLoader.AddTexture("ui_" + name + "_party",      "gui/" + name + "/party.jpg");
            AssetLoader.AddTexture("ui_" + name + "_shop",       "gui/" + name + "/shop.jpg");
            AssetLoader.AddTexture("ui_" + name + "_skills",     "gui/" + name + "/skills.jpg");
            AssetLoader.AddTexture("ui_" + name + "_trade",      "gui/" + name + "/trade.jpg");

            AssetLoader.AddTexture("ui_" + name + "_health_bar",       "gui/" + name + "/bars/health.jpg");
            AssetLoader.AddTexture("ui_" + name + "_spirit_bar",       "gui/" + name + "/bars/spirit.jpg");
            AssetLoader.AddTexture("ui_" + name + "_exp_bar",          "gui/" + name + "/bars/experience.jpg");
            AssetLoader.AddTexture("ui_" + name + "_party_health_bar", "gui/" + name + "/bars/party_health.jpg");
            AssetLoader.AddTexture("ui_" + name + "_party_spirit_bar", "gui/" + name + "/bars/party_spirit.jpg");

            loadButtonTextures(name, "char");
            loadButtonTextures(name, "exit");
            loadButtonTextures(name, "inv");
            loadButtonTextures(name, "opt");
            loadButtonTextures(name, "party");
            loadButtonTextures(name, "skills");
            loadButtonTextures(name, "trade");
        } else if(name.equalsIgnoreCase("menu")) {
            AssetLoader.AddTexture("ui_" + name + "_background", "gui/" + name + "/background.jpg");
            AssetLoader.AddTexture("ui_" + name + "_character",  "gui/" + name + "/character.jpg");
            AssetLoader.AddTexture("ui_" + name + "_credits",    "gui/" + name + "/credits.jpg");
            AssetLoader.AddTexture("ui_" + name + "_loading",    "gui/" + name + "/loading.jpg");
            AssetLoader.AddTexture("ui_" + name + "_login",      "gui/" + name + "/login.jpg");
            AssetLoader.AddTexture("ui_" + name + "_main",       "gui/" + name + "/main.jpg");
            AssetLoader.AddTexture("ui_" + name + "_register",   "gui/" + name + "/register.jpg");

            loadButtonTextures(name, "credits");
            loadButtonTextures(name, "exit");
            loadButtonTextures(name, "login");
            loadButtonTextures(name, "register");
        }
    }

    /**
     * Loads the button textures in the relative paths to the menu fold you specify.</br>
     * Keep in mind, this loads the click, hover, and idle versions of the button, no need to do it separately.
     *
     * @param uiName the name of the folder we search the GUI folder for
     * @param name name of the button you want to add.
     */
    private void loadButtonTextures(String uiName, String name) {
        AssetLoader.AddTexture("btn_" + uiName + "_" + name, "gui/" + uiName + "/buttons/" + name + "_click.jpg");
        AssetLoader.AddTexture("btn_" + uiName + "_" + name, "gui/" + uiName + "/buttons/" + name + "_hover.jpg");
        AssetLoader.AddTexture("btn_" + uiName + "_" + name, "gui/" + uiName + "/buttons/" + name + "_norm.jpg");
    }

    private void loadNonUITextures(String name, int amount) {
        for(var i = 1; i <= amount; ++i)
            AssetLoader.AddTexture(name + i, name + "s/" + i + ".bmp");
    }
}
