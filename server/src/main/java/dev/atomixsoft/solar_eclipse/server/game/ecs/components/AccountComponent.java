package dev.atomixsoft.solar_eclipse.server.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class AccountComponent implements Component, Pool.Poolable {
    public int accountId;

    public AccountComponent() {
        accountId = -1;
    }

    @Override
    public void reset() {
        accountId = -1;
    }
}
