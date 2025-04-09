package dev.atomixsoft.solar_eclipse.core.game;

import dev.atomixsoft.solar_eclipse.core.game.character.Character;
import dev.atomixsoft.solar_eclipse.core.game.map.GameMap;
import dev.atomixsoft.solar_eclipse.core.game.map.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * This static class provides an easy way to handle data between the client/server and editor/server.
 */
public class Actuator {

    // Accounts and Characters

    public static Account CreateAccount(String user, String pass) {
        Account account = new Account();

        account.username = user;
        account.password = pass;

        return account;
    }

    public static Account LoadAccount(String user, String pass) {
        // TODO: Implement user/pass checking
        return null;
    }

    public static Character CreatePlayerCharacter(Account acc, String name, int texId, byte sex) {
        if(acc == null || acc.characters.size() >= 3) return null;

        Character newChar = new Character();

        newChar.textureId = texId;
        newChar.name = name;
        newChar.sex = sex;

        acc.characters.add(newChar);
        return newChar;
    }

    public static Character LoadPlayerCharacter(Account acc, int index) {
        if(acc == null) return null;
        return acc.characters.get(index);
    }

    public static Character LoadPlayerCharacter(Account acc, String name) {
        if(acc == null) return null;

        for(Character ch : acc.characters) {
            if(ch == null) continue;

            if(ch.name.equals(name))
                return LoadPlayerCharacter(acc, acc.characters.indexOf(ch));
        }

        return null;
    }

    // Tiles and Maps

    public static Tile GetTileFromMap(GameMap map, int layer, int x, int y) {
        if(layer >= Constants.MAX_MAP_LAYERS) return null;

        for(Tile tile : map.TileMap.get(layer)) {
            if(tile == null) continue;

            if (tile.x == x && tile.y == y)
                return tile;
        }

        return null;
    }

    public static void AddTileToMap(GameMap map, Tile tileData, int layer, int x, int y) {
        if(map == null) return;

        boolean exists = map.TileMap.get(layer) != null;

        if(!exists) CreateNewLayer(map, layer);
        List<Tile> layerTiles = map.TileMap.get(layer);
        assert(layerTiles != null);

        Tile tile = new Tile(tileData);
        tile.x = x;
        tile.y = y;

        layerTiles.add(x * map.width + y, tile);

        if(exists) map.TileMap.replace(layer, layerTiles);
        else map.TileMap.put(layer, layerTiles);
    }

    public static void CreateNewLayer(GameMap map, int layer) {
        if(layer > Constants.MAX_MAP_LAYERS) return;

        List<Tile> layerTiles = new ArrayList<>();
        for(var x = 0; x < map.width; ++x) {
            for(var y = 0; y < map.height; ++y) {
                layerTiles.add(null);
            }
        }

        map.TileMap.put(layer, layerTiles);
    }

    public static void FillMapLayer(GameMap map, Tile tileData, int layer) {
        if(map == null) return;

        boolean exists = map.TileMap.get(layer) != null;

        if(!exists) CreateNewLayer(map, layer);

        List<Tile> layerTiles = map.TileMap.get(layer);
        assert(layerTiles != null);
        for(var x = 0; x < map.width; ++x) {
            for(var y = 0; y < map.height; ++y) {
                Tile tile = new Tile(tileData);
                tile.x = x;
                tile.y = y;

                layerTiles.add(x * map.width + y, tile);
            }
        }

        if(exists) map.TileMap.replace(layer, layerTiles);
        else map.TileMap.put(layer, layerTiles);
    }

    // Character portion of the map part

    public static void AddCharacterToMap(GameMap map, Character character, int x, int y) {
        if(map == null || character == null) return;

        character.x = x;
        character.y = y;

        for(Character others : map.MapCharacters) {
            if(!character.player) break;
            if(!others.player) continue;

            if(others.equals(character)) {
                System.err.println("Player Character exists on this map already!");
                return;
            }
        }

        map.MapCharacters.add(character);
    }

    public static void AddCharactersToMap(GameMap map, List<Character> characters, int x, int y) {
        for(Character c : characters)
            AddCharacterToMap(map, c, x, y);
    }

    public static void MoveCharacter(GameMap map, Character character, int x, int y) {
        if (x > 0) character.facing = Character.Direction.RIGHT;
        else if(x < 0) character.facing = Character.Direction.LEFT;

        if (y > 0) character.facing = Character.Direction.UP;
        else if(y < 0) character.facing = Character.Direction.DOWN;

        if(character.moving) return;
        character.moving = true;

        if(!map.MapCharacters.contains(character)) {
            if(x < 0) x = 0;
            if(x >= map.width) x = map.width - 1;

            if(y < 0) y = 0;
            if(y >= map.height) y = map.height - 1;

            AddCharacterToMap(map, character, x, y);
            character.moving = false;
            return;
        }

        int xOffs = character.x + x, yOffs = character.y + y;
        if(xOffs <= 0) xOffs = 0;
        else if(xOffs >= map.width) xOffs = map.width - 1;

        if(yOffs <= 0) yOffs = 0;
        else if(yOffs >= map.height) yOffs = map.height - 1;

        Tile destination = Actuator.GetTileFromMap(map, 0, xOffs, yOffs);
        if(destination == null) {
            character.moving = false;
            return;
        }

        if(destination.type == Constants.TILE_TYPE_WALKABLE) {
            character.x += x;
            character.y += y;

            if(character.x <= 0) character.x = 0;
            else if(character.x >= map.width) character.x = map.width - 1;

            if(character.y <= 0) character.y = 0;
            else if(character.y >= map.height) character.y = map.height - 1;

            character.moving = true;
        }

    }

    // Item portion of the map part

    public static Item GetItemOnMap(GameMap map, int x, int y) {
        if(map == null) return null;

        for(Item it : map.WorldItems) {
            if(it.inWorld && (it.worldX == x && it.worldY == y))
                return it;
        }

        return null;
    }

    public static Item AddItemToMap(GameMap map, Item item, int x, int y) {
        if(map == null || item == null) return null;

        // Check if there is a version of that item there and if it is stackable
        int leftOverStack = 0;
        Item other = GetItemOnMap(map, x, y);
        if(other != null) {
            // Add the amount specified of if amount is 0, the stack count of that item
            if(other.name.equalsIgnoreCase(item.name) && other.stackable) {
                int stackSum = other.stack + item.stack;
                if(stackSum > other.maxStack) {
                    leftOverStack = stackSum - other.maxStack;
                    other.stack = other.maxStack;
                } else {
                    other.stack += item.stack;
                    return other;
                }
            }
        }

        item.stack = leftOverStack <= 0 ? 1 : leftOverStack;
        item.worldX = x;
        item.worldY = y;
        item.inWorld = true;

        map.WorldItems.add(item);
        return item;
    }

}
