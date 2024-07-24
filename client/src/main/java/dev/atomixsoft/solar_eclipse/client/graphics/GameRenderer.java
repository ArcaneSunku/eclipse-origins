package dev.atomixsoft.solar_eclipse.client.graphics;

import dev.atomixsoft.solar_eclipse.client.AssetLoader;

import dev.atomixsoft.solar_eclipse.client.ClientThread;
import dev.atomixsoft.solar_eclipse.client.graphics.render2D.Sprite;
import dev.atomixsoft.solar_eclipse.client.graphics.render2D.SpriteBatch;
import dev.atomixsoft.solar_eclipse.core.game.Actuator;
import dev.atomixsoft.solar_eclipse.core.game.Constants;
import dev.atomixsoft.solar_eclipse.core.game.Item;
import dev.atomixsoft.solar_eclipse.core.game.character.Character;
import dev.atomixsoft.solar_eclipse.core.game.map.GameMap;
import dev.atomixsoft.solar_eclipse.core.game.map.Tile;

import java.util.List;

/**
 * <p>Handles rendering for the visible Game World on the client side. (NPCs, PCs, Tiles, Resources, etc)</p>
 */
public class GameRenderer {
    private static final int SPRITE_CELL_SIZE = 32;
    public static final int TILE_SIZE = 16;

    private GameMap m_Map;
    public GameRenderer() { }

    public GameRenderer(GameMap map) {
        this();
        m_Map = map;
    }

    public void render(SpriteBatch batch) {
        if(m_Map == null) return;
        int numLayers = Math.min(m_Map.TileMap.keySet().size(), Constants.MAX_MAP_LAYERS);

        renderTiles(batch, numLayers, false);
        renderItems(batch, m_Map.WorldItems);
        renderCharacters(batch, m_Map.MapCharacters);
        renderTiles(batch, numLayers, true);
    }

    private void renderTiles(SpriteBatch batch, int layers, boolean roofs) {
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

                    sprite.setPosition(x * TILE_SIZE, y * TILE_SIZE, 0);
                    sprite.setSize(TILE_SIZE, TILE_SIZE);

                    sprite.draw(batch);
                }
            }
        }
    }

    private void renderItems(SpriteBatch batch, List<Item> worldItems) {
        int count = 0;
        for(var i = 0; i < worldItems.size(); ++i) {
            Item item = worldItems.get(i);
            if(item == null || !item.inWorld) continue;

            Sprite itemSprite = new Sprite(AssetLoader.GetTexture("items" + i));
            itemSprite.setCellPos(0, 0);
            itemSprite.setCellSize(32, 32);

            itemSprite.setPosition(item.worldX * TILE_SIZE, item.worldY * TILE_SIZE, 0);
            itemSprite.setSize(TILE_SIZE, TILE_SIZE);

            itemSprite.draw(batch);
            count++;
        }

        if(count != 0)
            ClientThread.log().info("Items Rendered: " + count + ", Items ");
    }

    private void renderCharacters(SpriteBatch batch, List<Character> charList) {
        for(var i = 0; i < charList.size(); ++i) {
            Character c = charList.get(i);
            if(c == null || c.removed) continue;

            int keyFrame = Math.max(0, Math.min(c.keyFrame, 3));
            Sprite sprite = new Sprite(AssetLoader.GetTexture("char" + c.textureId));

            float cellWidth = sprite.getTexture().getWidth() / 4.0f, cellHeight = sprite.getTexture().getHeight() / 4.0f;
            sprite.setCellSize(cellWidth, cellHeight);

            switch (c.facing) {
                case UP -> sprite.setCellPos(keyFrame * cellWidth, cellHeight * 3);
                case DOWN -> sprite.setCellPos(keyFrame * cellWidth, 0);
                case LEFT -> sprite.setCellPos(keyFrame * cellWidth, cellHeight);
                case RIGHT -> sprite.setCellPos(keyFrame * cellWidth, cellHeight * 2);
            }

            sprite.setPosition(c.x * cellWidth, c.y * cellHeight + 16 + 8, 0);
            sprite.setSize(cellWidth, cellHeight);

            sprite.draw(batch);
        }
    }

    public void setMap(GameMap newMap) {
        this.m_Map = newMap;
    }

    public GameMap getMap() {
        return m_Map;
    }

}
