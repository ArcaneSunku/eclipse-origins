package dev.atomixsoft.solar_eclipse.server.net.services;

import dev.atomixsoft.solar_eclipse.core.game.Actuator;
import dev.atomixsoft.solar_eclipse.core.game.Constants;
import dev.atomixsoft.solar_eclipse.core.game.map.GameMap;
import dev.atomixsoft.solar_eclipse.core.game.map.Tile;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.MapLoad;

import java.util.HashMap;
import java.util.Map;

public class MapService {

    private final Map<Integer, GameMap> m_Maps;

    public MapService() {
        m_Maps = new HashMap<>();
        createTestGrassMap();
    }

    private void createTestGrassMap() {
        GameMap map = new GameMap(0, 0, 17, 13);
        map.id = 0;

        Tile grassTile = new Tile();
        grassTile.textureId = 1;
        grassTile.textureX = 0;
        grassTile.textureY = 1;
        grassTile.type = Constants.TILE_TYPE_WALKABLE;
        grassTile.roof = false;

        Actuator.FillMapLayer(map, grassTile, 0);

        m_Maps.put(0, map);
    }

    public GameMap getMap(int id) {
        return m_Maps.get(id);
    }

    public MapLoad createMapLoad(int id) {
        GameMap map = getMap(id);

        if(map == null)
            return null;

        Tile base = Actuator.GetTileFromMap(map, 0, 0, 0);

        if(base == null)
            return null;

        return new MapLoad(map.id, map.width, map.height, base.textureId, base.textureX, base.textureY, base.type);
    }

}
