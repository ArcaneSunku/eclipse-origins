package dev.atomixsoft.solar_eclipse.server.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class SpriteComponent implements Component, Pool.Poolable {

    public int textureId;

    public SpriteComponent() {
        textureId = -1;
    }

    @Override
    public void reset() {
        textureId = -1;
    }
}
