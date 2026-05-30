package dev.atomixsoft.solar_eclipse.client.graphics;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;

import dev.atomixsoft.solar_eclipse.client.ClientThread;
import dev.atomixsoft.solar_eclipse.client.graphics.cameras.OrthoCamera;
import dev.atomixsoft.solar_eclipse.client.graphics.render2D.Sprite;
import dev.atomixsoft.solar_eclipse.client.graphics.render2D.SpriteBatch;
import dev.atomixsoft.solar_eclipse.core.game.Actuator;
import dev.atomixsoft.solar_eclipse.core.game.Constants;
import dev.atomixsoft.solar_eclipse.core.game.Item;
import dev.atomixsoft.solar_eclipse.core.game.character.Character;
import dev.atomixsoft.solar_eclipse.core.game.map.GameMap;
import dev.atomixsoft.solar_eclipse.core.game.map.Tile;
import org.joml.Math;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.List;

/**
 * <p>Handles rendering for the visible Game World on the client side. (NPCs, PCs, Tiles, Resources, etc)</p>
 */
public class GameRenderer {
    private static final int SPRITE_CELL_SIZE = 32;
    public static final int TILE_SIZE = 32;

    private GameMap m_Map;
    public GameRenderer() { }

    public GameRenderer(GameMap map) {
        this();
        m_Map = map;
    }

    public void update(OrthoCamera camera) {
        Vector3f pos = camera.getPosition();

        float halfW = camera.getViewWidth();
        float halfH = camera.getViewHeight();

        float worldW = getMapWidth() * TILE_SIZE;
        float worldH = getMapHeight() * TILE_SIZE;

        // IMPORTANT: world is now center-based per tile system
        float minX = halfW;
        float minY = halfH;

        float maxX = worldW - halfW;
        float maxY = worldH - halfH;

        pos.x = Math.clamp(minX, maxX, pos.x);
        pos.y = Math.clamp(minY, maxY, pos.y);
    }

    public void render(SpriteBatch batch, OrthoCamera camera) {
        if(m_Map == null) return;
        int numLayers = Math.min(m_Map.TileMap.size(), Constants.MAX_MAP_LAYERS);

        renderTiles(batch, camera, numLayers, false);
        renderItems(batch, camera, m_Map.WorldItems);
        renderCharacters(batch, camera, m_Map.MapCharacters);
        renderTiles(batch, camera, numLayers, true);
    }

    private void renderTiles(SpriteBatch batch, OrthoCamera camera, int layers, boolean roofs) {
        for(var layer = 0; layer < layers; ++layer) {
            for(var x = 0; x < m_Map.width; ++x) {
                for(var y = 0; y < m_Map.height; ++y) {
                    Tile currTile = Actuator.GetTileFromMap(m_Map, layer, x, y);
                    if(currTile == null) continue;

                    // Roof Check
                    if(roofs && !currTile.roof) continue;
                    else if(!roofs && currTile.roof) continue;

                    // Creates sprite to render based on the Tile information
                    Sprite sprite = new Sprite(AssetLoader.GetTexture("tileset" + currTile.textureId));
                    sprite.setCellPos(currTile.textureX * SPRITE_CELL_SIZE, currTile.textureY * SPRITE_CELL_SIZE);
                    sprite.setCellSize(SPRITE_CELL_SIZE, SPRITE_CELL_SIZE);

                    sprite.setPosition(x * TILE_SIZE + TILE_SIZE * 0.5f, y * TILE_SIZE + TILE_SIZE * 0.5f, 0);
                    sprite.setSize(TILE_SIZE, TILE_SIZE);

                    if(!isVisible(sprite, camera))
                        continue;

                    sprite.draw(batch);
                }
            }
        }
    }

    private void renderItems(SpriteBatch batch, OrthoCamera camera, List<Item> worldItems) {
        int count = 0;
        for(var i = 0; i < worldItems.size(); ++i) {
            Item item = worldItems.get(i);
            if(item == null || !item.inWorld) continue;

            Sprite itemSprite = new Sprite(AssetLoader.GetTexture(item.textureName));
            itemSprite.setCellPos(0, 0);
            itemSprite.setCellSize(32, 32);

            itemSprite.setPosition(item.worldX * TILE_SIZE + TILE_SIZE * 0.5f, item.worldY * TILE_SIZE + TILE_SIZE * 0.5f, 0);
            itemSprite.setSize(TILE_SIZE, TILE_SIZE);

            if(!isVisible(itemSprite, camera))
                continue;

            itemSprite.draw(batch);
            count++;
        }
    }

    private void renderCharacters(SpriteBatch batch, OrthoCamera camera, List<Character> charList) {
        charList.sort(Comparator.comparingInt(c -> -c.y));

        for(var i = 0; i < charList.size(); ++i) {
            Character c = charList.get(i);
            if(c == null || c.removed) continue;

            int keyFrame = c.keyFrame;
            Sprite sprite = new Sprite(AssetLoader.GetTexture("character" + c.textureId));

            float cellWidth = sprite.getTexture().getWidth() / 4.0f, cellHeight = sprite.getTexture().getHeight() / 4.0f;
            sprite.setCellSize(cellWidth, cellHeight);

            switch (c.facing) {
                case DOWN  -> sprite.setCellPos(keyFrame * cellWidth, 0);
                case LEFT  -> sprite.setCellPos(keyFrame * cellWidth, cellHeight);
                case RIGHT -> sprite.setCellPos(keyFrame * cellWidth, cellHeight * 2);
                case UP    -> sprite.setCellPos(keyFrame * cellWidth, cellHeight * 3);
            }

            float renderWidth  = cellWidth * 1.5f;
            float renderHeight = cellHeight * 1.5f;

            sprite.setSize(renderWidth, renderHeight);

            float tileCenterX = c.x * TILE_SIZE + TILE_SIZE * 0.5f;
            float tileCenterY = c.y * TILE_SIZE + TILE_SIZE * 0.5f;

            float feetY = tileCenterY;

            sprite.setPosition(tileCenterX, feetY + renderHeight * 0.5f - TILE_SIZE * 0.5f, 0);

            Vector3f min = new Vector3f(), max = new Vector3f();
            camera.getBounds(min, max);

            if(!isVisible(sprite, camera))
                continue;

            sprite.draw(batch);
        }
    }

    private boolean isVisible(Sprite sprite, OrthoCamera camera) {
        Vector3f min = new Vector3f();
        Vector3f max = new Vector3f();

        camera.getBounds(min, max);

        float halfW = sprite.getSize().x * 0.5f;
        float halfH = sprite.getSize().y * 0.5f;

        float sx = sprite.getPosition().x;
        float sy = sprite.getPosition().y;

        float spriteMinX = sx - halfW;
        float spriteMaxX = sx + halfW;

        float spriteMinY = sy - halfH;
        float spriteMaxY = sy + halfH;

        return !(spriteMaxX < min.x ||
                spriteMinX > max.x ||
                spriteMaxY < min.y ||
                spriteMinY > max.y);
    }

    public void setMap(GameMap newMap) {
        this.m_Map = newMap;
    }

    public GameMap getMap() {
        return m_Map;
    }

    public int getMapWidth() {
        return m_Map.width;
    }

    public int getMapHeight() {
        return m_Map.height;
    }

}
