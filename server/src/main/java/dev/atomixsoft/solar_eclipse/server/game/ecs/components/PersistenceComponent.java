package dev.atomixsoft.solar_eclipse.server.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class PersistenceComponent implements Component, Pool.Poolable {

    public int accountId, characterId;

    public PersistenceComponent() {
        accountId = -1;
        characterId = -1;
    }

    @Override
    public void reset() {
        accountId = -1;
        characterId = -1;
    }
}
