package dev.atomixsoft.solar_eclipse.client.scene;

import dev.atomixsoft.solar_eclipse.client.ClientThread;
import dev.atomixsoft.solar_eclipse.client.graphics.FrameBuffer;
import dev.atomixsoft.solar_eclipse.client.graphics.GameRenderer;
import dev.atomixsoft.solar_eclipse.client.graphics.RenderCmd;
import dev.atomixsoft.solar_eclipse.client.graphics.Texture;
import dev.atomixsoft.solar_eclipse.client.graphics.ui.GameMenu;
import dev.atomixsoft.solar_eclipse.client.graphics.ui.Hotbar;
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
    private GameMenu gameMenu;
    private boolean focused;

    private Character player;

    @Override
    public void show() {
        camera = new OrthoCamera(476, 380);
        camera.setZoom(16 * 9);

        batch = new SpriteBatch(AssetLoader.GetShader("basic"));

        frameBuffer = new FrameBuffer(476, 380);
        gameRender = new GameRenderer();
        gameMenu = new GameMenu();

        GameMap testMap = new GameMap(0, 0, 12, 10);

        Tile grassTile = new Tile();
        grassTile.textureId = 1;
        grassTile.textureX = 0;
        grassTile.textureY = 1;

        grassTile.type = Constants.TILE_TYPE_WALKABLE;
        grassTile.roof = false;

        Actuator.FillMapLayer(testMap, grassTile, 0);

        player = new Character();
        player.name = "Dev";
        player.player = true;

        Actuator.AddCharacterToMap(testMap, player, 2, 3);

        gameRender.setMap(testMap);
        focused = true;
    }

    private float frameTime = 0;
    private float tickTime = 0;

    @Override
    public void update(Controller input, double dt) {
        if(input.isPressed(InputType.CANCEL)) {
            ClientThread.set_size(515, 352);
            ClientThread.set_scene("Menu");
            return;
        }
        tickTime += (float) dt;


        if(!player.moving) {
            if (input.isPressed(InputType.UP)) {
                Actuator.MoveCharacter(gameRender.getMap(), player, 0, 1);
            } else if (input.isPressed(InputType.DOWN)) {
                Actuator.MoveCharacter(gameRender.getMap(), player, 0, -1);
            } else if (input.isPressed(InputType.LEFT)) {
                Actuator.MoveCharacter(gameRender.getMap(), player, -1, 0);
            } else if (input.isPressed(InputType.RIGHT)) {
                Actuator.MoveCharacter(gameRender.getMap(), player, 1, 0);
            }
        }

        if(tickTime >= 0.65f) {
            boolean updated = false;
            if(!input.MovementInput()) {
                player.moving = false;
                tickTime = 0.0f;
                updated = true;
            }

            if(!updated) {
                if (player.moving) player.moving = false;
                tickTime = 0.0f;
            }
        }

        if(player.moving) {
            frameTime += (float) dt;

            if(frameTime >= 0.35f) {
                if(player.keyFrame == 0) player.keyFrame = 1;
                if(player.keyFrame == 1) player.keyFrame += 2;
                if(player.keyFrame == 3) player.keyFrame = 1;

                frameTime = 0.0f;
            }
        } else {
            player.keyFrame = 0;
        }

        gameRender.update(camera);
    }

    @Override
    public void render() {

        frameBuffer.bind();
        glViewport(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
        RenderCmd.ClearColor(0.05f, 0.05f, 0.05f);
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

        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(main_bg.getWidth(), main_bg.getHeight());

        ImGui.begin("Background", ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoBringToFrontOnFocus);
        // Background Image
        ImGui.image(main_bg.getTextureId(), ImGui.getContentRegionAvail());

        // Game Buffer
        ImGui.setNextWindowPos(12, 12);
        ImGui.setNextWindowSize(frameBuffer.getWidth(), frameBuffer.getHeight());

        ImGui.begin("Game_Window", ImGuiWindowFlags.NoDecoration);
        ImGui.image(frameBuffer.getColorBufferId(), ImGui.getContentRegionAvail(), new ImVec2(0, 1), new ImVec2(1, 0));
        focused = ImGui.isWindowFocused();
        ImGui.end();

        gameMenu.render(gameRender);

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
