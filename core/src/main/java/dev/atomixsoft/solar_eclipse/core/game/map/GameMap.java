package dev.atomixsoft.solar_eclipse.core.game.map;

import dev.atomixsoft.solar_eclipse.core.game.Constants;
import dev.atomixsoft.solar_eclipse.core.game.Item;
import dev.atomixsoft.solar_eclipse.core.game.character.Character;

import java.util.*;

public class GameMap {

    public final Map<Integer, List<Tile>> TileMap = new HashMap<>();
    public final List<Character> MapCharacters = new ArrayList<>();
    public final List<Item> WorldItems = new ArrayList<>();

    public byte id;
    public byte moral;
    public int x, y;
    public int width, height;

    public GameMap(int x, int y, int width, int height) {
        this.moral = Constants.MAP_MORAL_NONE;
        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameMap gameMap = (GameMap) o;

        return moral == gameMap.moral &&
               x == gameMap.x && y == gameMap.y &&
               width == gameMap.width && height == gameMap.height &&
               Objects.equals(TileMap, gameMap.TileMap) &&
               Objects.equals(MapCharacters, gameMap.MapCharacters) &&
               Objects.equals(WorldItems, gameMap.WorldItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(TileMap, MapCharacters, WorldItems, moral, x, y, width, height);
    }
}
