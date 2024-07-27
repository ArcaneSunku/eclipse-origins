package dev.atomixsoft.solar_eclipse.client.scene;

import dev.atomixsoft.solar_eclipse.client.graphics.GameRenderer;
import dev.atomixsoft.solar_eclipse.core.game.Actuator;
import dev.atomixsoft.solar_eclipse.core.game.character.Character;
import dev.atomixsoft.solar_eclipse.core.game.map.GameMap;
import dev.atomixsoft.solar_eclipse.core.game.map.Tile;
import imgui.ImGui;
import org.joml.Vector3f;

import dev.atomixsoft.solar_eclipse.core.game.Constants;

import dev.atomixsoft.solar_eclipse.client.util.input.Controller;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;

import dev.atomixsoft.solar_eclipse.client.graphics.render2D.SpriteBatch;
import dev.atomixsoft.solar_eclipse.client.graphics.cameras.OrthoCamera;

import static dev.atomixsoft.solar_eclipse.core.event.types.InputEvent.InputType;

/**
 * <p>Purely for prototyping features in the earlier stages of development.</p>
 */
public class TestScene extends SceneAdapter{

    private OrthoCamera camera;
    private SpriteBatch batch;
    private GameRenderer mapRender;

    int mapSize = 10;

    @Override
    public void show() {
        mapRender = new GameRenderer();
        camera = new OrthoCamera(800, 600);

        AssetLoader.AddShader("basic", "basic");
        batch = new SpriteBatch(AssetLoader.GetShader("basic"));

        camera.setZoom(3);
        Vector3f pos = camera.getPosition();
        pos.x = 0;
        pos.y = 0;
        camera.setPosition(pos);

        AssetLoader.AddTexture("tileset1", "tilesets/1.bmp");
        AssetLoader.AddTexture("tileset2", "tilesets/2.bmp");

        AssetLoader.AddTexture("char1", "characters/1.bmp");
        AssetLoader.AddTexture("char2", "characters/2.bmp");
        AssetLoader.AddTexture("char3", "characters/3.bmp");

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

        Actuator.FillMapLayer(testMap, grassTile, 0);

        Actuator.AddTileToMap(testMap, trunkTile, 1, 2, 2);
        Actuator.AddTileToMap(testMap, trunkTile, 1, 10, 8);
        Actuator.AddTileToMap(testMap, trunkTile, 1, 10, 13);

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

        if(position.x < (camera.getWidth() - 8  * camera.getZoom()) / (camera.getAspectRatio()))
            position.x = (camera.getWidth() - 8  * camera.getZoom()) / (camera.getAspectRatio());

        if(position.y < (camera.getHeight() - 8 * camera.getZoom()) / (camera.getAspectRatio()))
            position.y = (camera.getHeight() - 8 * camera.getZoom()) / (camera.getAspectRatio());

        camera.setPosition(position);
    }

    @Override
    public void render() {
        batch.begin(camera);

        mapRender.render(batch);

        batch.end();
    }

    @Override
    public void imgui() {
        ImGui.begin("Test Inventory");

        ImGui.end();
    }

    @Override
    public void dispose() {
        if(batch != null)
            batch.dispose();
    }
}
