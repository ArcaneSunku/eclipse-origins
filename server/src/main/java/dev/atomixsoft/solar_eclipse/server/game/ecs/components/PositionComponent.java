package dev.atomixsoft.solar_eclipse.server.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class PositionComponent implements Component, Pool.Poolable {
    public int x, y;

    public PositionComponent() {
        x = y = 0;
    }

    @Override
    public void reset() {
        x = y = 0;
    }
}
