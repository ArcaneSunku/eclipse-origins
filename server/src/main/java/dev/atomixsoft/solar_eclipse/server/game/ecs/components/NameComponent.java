package dev.atomixsoft.solar_eclipse.server.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class NameComponent implements Component, Pool.Poolable {

    public String name;

    public NameComponent() {
        name = "";
    }

    @Override
    public void reset() {
        name = "";
    }
}
