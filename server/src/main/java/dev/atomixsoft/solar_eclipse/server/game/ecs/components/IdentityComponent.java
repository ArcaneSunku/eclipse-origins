package dev.atomixsoft.solar_eclipse.server.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class IdentityComponent implements Component, Pool.Poolable {
    public int entityId;

    public IdentityComponent() {
        entityId = 0;
    }

    @Override
    public void reset() {
        entityId = 0;
    }
}
