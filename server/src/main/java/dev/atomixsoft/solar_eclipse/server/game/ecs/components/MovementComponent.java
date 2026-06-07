package dev.atomixsoft.solar_eclipse.server.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class MovementComponent implements Component, Pool.Poolable {
    public int dx, dy;
    public float moveCooldown;

    public byte direction;
    public boolean moving;

    public MovementComponent() {
        dx = dy = 0;
        moveCooldown = 0.0f;

        direction = 1;
        moving = false;
    }

    @Override
    public void reset() {
        dx = dy = 0;
        moving = false;
    }
}
