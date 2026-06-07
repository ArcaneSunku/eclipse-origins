package dev.atomixsoft.solar_eclipse.client.scene;

import dev.atomixsoft.solar_eclipse.client.ClientThread;
import dev.atomixsoft.solar_eclipse.client.graphics.FrameBuffer;
import dev.atomixsoft.solar_eclipse.client.graphics.GameRenderer;
import dev.atomixsoft.solar_eclipse.client.graphics.RenderCmd;
import dev.atomixsoft.solar_eclipse.client.graphics.Texture;
import dev.atomixsoft.solar_eclipse.client.graphics.ui.ChatBox;
import dev.atomixsoft.solar_eclipse.client.graphics.ui.GameMenu;
import dev.atomixsoft.solar_eclipse.core.event.types.SendPacketEvent;
import dev.atomixsoft.solar_eclipse.core.game.Actuator;
import dev.atomixsoft.solar_eclipse.core.game.character.CharacterData;
import dev.atomixsoft.solar_eclipse.core.game.character.Direction;
import dev.atomixsoft.solar_eclipse.core.game.map.GameMap;
import dev.atomixsoft.solar_eclipse.core.game.map.Tile;
import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.ChatMessageBroadcast;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.ChatMessageRequest;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.MoveIntent;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.EntityPositionUpdate;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.MapLoad;
import imgui.*;
import imgui.flag.*;

import dev.atomixsoft.solar_eclipse.core.game.Constants;

import dev.atomixsoft.solar_eclipse.client.util.input.Controller;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;

import dev.atomixsoft.solar_eclipse.client.graphics.render2D.SpriteBatch;
import dev.atomixsoft.solar_eclipse.client.graphics.cameras.OrthoCamera;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.atomixsoft.solar_eclipse.core.event.types.InputEvent.InputType;

/**
 * <p>Purely for prototyping features in the earlier stages of development.</p>
 */
public class TestScene extends SceneAdapter {

    private static final float MOVE_REQUEST_COOLDOWN = 0.25f;

    private float moveRequestCooldown;
    private int moveSequence;

    private OrthoCamera camera;
    private SpriteBatch batch;
    private FrameBuffer frameBuffer;

    private GameRenderer gameRender;
    private GameMenu gameMenu;
    private ChatBox chatBox;
    private boolean focused;

    private Map<Integer, CharacterData> entitiesById;
    private int localEntityId = -1;

    private CharacterData player;

    @Override
    public void show() {
        ClientThread.set_size(785, 594);
        if(entitiesById == null)
            entitiesById = new HashMap<>();
        else entitiesById.clear();

        batch = new SpriteBatch(AssetLoader.GetShader("basic"));

        frameBuffer = new FrameBuffer(544, 416);
        camera = new OrthoCamera(frameBuffer.getWidth(), frameBuffer.getHeight());
        camera.setZoom(0.0048f);

        gameRender = new GameRenderer();
        gameMenu = new GameMenu();

        moveRequestCooldown = 0.0f;
        moveSequence = 0;

        gameRender.setMap(null);
        focused = true;

        chatBox = new ChatBox();
    }

    private float frameTime = 0;
    private float tickTime = 0;

    @Override
    public void update(Controller input, double dt) {
        if(input.isPressed(InputType.ESCAPE)) {
            ClientThread.set_size(515, 352);
            ClientThread.set_scene("Menu");
            return;
        }

        tickTime += (float) dt;

        if(moveRequestCooldown > 0.0f) {
            moveRequestCooldown -= (float) dt;

            if(moveRequestCooldown < 0.0f)
                moveRequestCooldown = 0.0f;
        }

        if(player != null && moveRequestCooldown <= 0.0f) {
            int dx = 0, dy = 0;

            if (input.isPressed(InputType.UP)) {
                dy = 1;
            } else if (input.isPressed(InputType.DOWN)) {
                dy = -1;
            } else if (input.isPressed(InputType.LEFT)) {
                dx = -1;
            } else if (input.isPressed(InputType.RIGHT)) {
                dx = 1;
            }

            if(dx != 0 || dy != 0) {
                ClientThread.eventBus().post(new SendPacketEvent(new MoveIntent(dx, dy, moveSequence++)));
                moveRequestCooldown = MOVE_REQUEST_COOLDOWN;
            }
        }

        if(player != null) {
            player.keyFrame = 0;
        }

        gameRender.update(camera);
    }

    @Override
    public void handlePacket(Packet packet) {
        switch (packet) {
            case EntityPositionUpdate p -> {
                applyPositionUpdate(p);
            }

            case MapLoad p -> {
                applyMapLoad(p);
            }

            case ChatMessageBroadcast p -> {
                chatBox.update(p);
            }

            default -> {}
        }
    }

    private void applyPositionUpdate(EntityPositionUpdate packet) {
        if(gameRender.getMap() == null)
            return;

        CharacterData entity = entitiesById.get(packet.entityId());

        if(entity == null) {
            entity = new CharacterData();
            entity.name = "Entity " + packet.entityId();

            ClientThread.log().info("Entity " + packet.entityId() + " is being created...");

            Actuator.AddCharacterToMap(gameRender.getMap(), entity, packet.x(), packet.y());

            entitiesById.put(packet.entityId(), entity);

            if(player == null) {
                player = entity;
                localEntityId = packet.entityId();
            }
        }

        entity.x = packet.x();
        entity.y = packet.y();
        entity.facing = Direction.Get(packet.direction());
        entity.moving = false;
    }

    private void applyMapLoad(MapLoad packet) {
        GameMap map = new GameMap(0, 0, packet.width(), packet.height());
        map.id = (byte) packet.mapdId();

        Tile baseTile = new Tile();
        baseTile.textureId = packet.baseTexId();
        baseTile.textureX = packet.baseTexX();
        baseTile.textureY = packet.baseTexY();
        baseTile.type = packet.baseTileType();
        baseTile.roof = false;

        Actuator.FillMapLayer(map, baseTile, 0);

        entitiesById.clear();
        player = null;
        localEntityId = -1;

        gameRender.setMap(map);
    }

    @Override
    public void render() {
        frameBuffer.bind();
        RenderCmd.ClearColor(0.05f, 0.05f, 0.05f);
        RenderCmd.Clear();

        batch.begin(camera);
        gameRender.render(batch, camera);
        batch.end();
        frameBuffer.unbind();
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
        ImGui.end();

        // Game Buffer
        ImGui.setNextWindowPos(13, 12);
        ImGui.setNextWindowSize(475, 379);

        ImGui.begin("Game_Window", ImGuiWindowFlags.NoDecoration);
        ImGui.image(frameBuffer.getColorBufferId(), ImGui.getContentRegionAvail(), new ImVec2(0, 1), new ImVec2(1, 0));
        focused = ImGui.isWindowFocused();
        gameMenu.render(gameRender);
        ImGui.end();

        // Game Chat
        chatBox.render(player);

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
