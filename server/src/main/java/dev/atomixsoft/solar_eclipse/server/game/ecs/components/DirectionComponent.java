package dev.atomixsoft.solar_eclipse.server.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import dev.atomixsoft.solar_eclipse.core.game.character.Direction;

public class DirectionComponent implements Component, Pool.Poolable {
    public Direction direction;

    public DirectionComponent() {
        direction = Direction.DOWN;
    }

    @Override
    public void reset() {
        direction = Direction.DOWN;
    }

}
