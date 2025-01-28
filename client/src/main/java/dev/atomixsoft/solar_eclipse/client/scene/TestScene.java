package dev.atomixsoft.solar_eclipse.client.scene;

import dev.atomixsoft.solar_eclipse.client.ClientThread;
import dev.atomixsoft.solar_eclipse.client.graphics.GameRenderer;
import dev.atomixsoft.solar_eclipse.core.event.types.ShutdownEvent;
import dev.atomixsoft.solar_eclipse.core.game.Actuator;
import dev.atomixsoft.solar_eclipse.core.game.character.Character;
import dev.atomixsoft.solar_eclipse.core.game.map.GameMap;
import dev.atomixsoft.solar_eclipse.core.game.map.Tile;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.flag.ImGuiTableBgTarget;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiTableRowFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;

import dev.atomixsoft.solar_eclipse.core.game.Constants;

import dev.atomixsoft.solar_eclipse.client.util.input.Controller;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;

import dev.atomixsoft.solar_eclipse.client.graphics.render2D.SpriteBatch;
import dev.atomixsoft.solar_eclipse.client.graphics.cameras.OrthoCamera;

import javax.imageio.ImageIO;

import static dev.atomixsoft.solar_eclipse.core.event.types.InputEvent.InputType;

/**
 * <p>Purely for prototyping features in the earlier stages of development.</p>
 */
public class TestScene extends SceneAdapter{

    private OrthoCamera camera;
    private SpriteBatch batch;
    private GameRenderer mapRender;


    private ShutdownEvent sdEvent;

    private boolean debugMenu;
    private boolean exit;


    @Override
    public void show() {
        debugMenu = false;
        exit = false;

        sdEvent = new ShutdownEvent("dev", false);

        mapRender = new GameRenderer();
        camera = new OrthoCamera(800, 600);
        camera.setZoom(128.0f);

        AssetLoader.AddShader("basic", "basic");
        batch = new SpriteBatch(AssetLoader.GetShader("basic"));

        AssetLoader.AddTexture("tileset1", "tilesets/1.bmp");
        AssetLoader.AddTexture("tileset2", "tilesets/2.bmp");

        AssetLoader.AddTexture("char1", "characters/1.bmp");
        AssetLoader.AddTexture("char2", "characters/2.bmp");
        AssetLoader.AddTexture("char3", "characters/3.bmp");

        AssetLoader.AddTexture("item1", "items/1.bmp");
        AssetLoader.AddTexture("item2", "items/2.bmp");
        AssetLoader.AddTexture("item3", "items/3.bmp");
        AssetLoader.AddTexture("item4", "items/4.bmp");
        AssetLoader.AddTexture("item5", "items/5.bmp");
        AssetLoader.AddTexture("item6", "items/6.bmp");

        GameMap testMap = new GameMap(0, 0, 40, 40);

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
        Actuator.AddTileToMap(testMap, trunkTile, 1, 10, 8);
        Actuator.AddTileToMap(testMap, trunkTile, 1, 10, 13);
        Actuator.AddTileToMap(testMap, trunkTile, 1, 39, 2);
        Actuator.AddTileToMap(testMap, trunkTile, 1, 2, 39);

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
        testChar2.facing = Character.Direction.UP;
        testChar2.player = true;
        testChar2.sex = Constants.SEX_MALE;

        Actuator.AddCharacterToMap(testMap, testChar, 0, 0);
        Actuator.AddCharacterToMap(testMap, testChar2, 3, 10);

        mapRender.setMap(testMap);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void update(Controller input, double dt) {
        if(input.isPressed(InputType.CANCEL))
            exit = true;

        if(exit) {
            ClientThread.eventBus().post(sdEvent);
            return;
        }

        Vector3f position = camera.getPosition();
        float cameraSpeed = 300; // Adjust this as needed

        // Example control: move the camera with arrow keys
        if (input.isPressed(InputType.UP))
            position.y += (float) (cameraSpeed * dt);
        else if (input.isPressed(InputType.DOWN))
            position.y -= (float) (cameraSpeed * dt);

        if (input.isPressed(InputType.LEFT))
            position.x -= (float) (cameraSpeed * dt);
        else if (input.isPressed(InputType.RIGHT))
            position.x += (float) (cameraSpeed * dt);

        mapRender.update(camera);
    }

    @Override
    public void render() {
        batch.begin(camera);
        mapRender.render(batch, camera);
        batch.end();
    }

    @Override
    public void imgui() {
        ImGuiIO io = ImGui.getIO();

        ImVec2 winSize = ImGui.getWindowSize();

        ImGui.beginGroup();
        ImGui.begin("Hot Bar");

        ImGui.end();
        ImGui.endGroup();


        ImGui.setNextWindowSize(32 * 5.25f, 32 * 9.5f);
        ImGui.begin("Test Inventory", ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoBackground);

        int tableFlags = ImGuiTableFlags.SizingFixedFit | ImGuiTableFlags.Reorderable | ImGuiTableFlags.NoPadOuterX | ImGuiTableFlags.NoHostExtendX;
        if(ImGui.beginTable("#inventory", 4, tableFlags, 0, 0, 0)) {
            for(int i = 0; i < 8; i++) {
                ImGui.tableNextColumn();
                ImGui.tableSetBgColor(ImGuiTableBgTarget.CellBg, 0xff000000);
                ImGui.image(AssetLoader.GetTexture("item3").getTextureId(), 32, 32, 0.5f, 0, 1, 1);
                ImGui.tableNextColumn();
                ImGui.tableSetBgColor(ImGuiTableBgTarget.CellBg, 0xff000000);
                ImGui.image(AssetLoader.GetTexture("item2").getTextureId(), 32, 32, 0.5f, 0, 1, 1);
                ImGui.tableNextColumn();
                ImGui.tableSetBgColor(ImGuiTableBgTarget.CellBg, 0xff000000);
                ImGui.image(AssetLoader.GetTexture("item5").getTextureId(), 32, 32, 0.5f, 0, 1, 1);
                ImGui.tableNextColumn();
                ImGui.tableSetBgColor(ImGuiTableBgTarget.CellBg, 0xff000000);
                ImGui.image(AssetLoader.GetTexture("item4").getTextureId(), 32, 32, 0.5f, 0, 1, 1);
            }
            ImGui.endTable();
        }
        ImGui.end();
    }

    @Override
    public void dispose() {
        if(batch != null)
            batch.dispose();
    }
}
