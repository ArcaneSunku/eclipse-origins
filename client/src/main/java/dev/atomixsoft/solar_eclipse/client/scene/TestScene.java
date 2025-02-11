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

        batch = new SpriteBatch(AssetLoader.GetShader("basic"));

        frameBuffer = new FrameBuffer(476, 380);
        gameRender = new GameRenderer();

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

        if(frameBuffer != null)
            frameBuffer.dispose();
    }
}
