package dev.atomixsoft.solar_eclipse.server.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class PositionComponent implements Component, Pool.Poolable {
    public int x, y;
    public int mapId;

    public PositionComponent() {
        x = y = 0;
        mapId = -1;
    }

    @Override
    public void reset() {
        x = y = 0;
        mapId = -1;
    }
}
