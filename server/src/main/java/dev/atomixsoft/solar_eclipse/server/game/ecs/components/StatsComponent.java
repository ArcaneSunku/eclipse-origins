package dev.atomixsoft.solar_eclipse.server.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class StatsComponent implements Component, Pool.Poolable {
    public int level;
    public int health, maxHealth;
    public int spirit, maxSpirit;
    public int experience, maxExperience;
    public int gold;

    public StatsComponent() {
        level = 1;
        health = maxHealth = 100;
        spirit = maxSpirit = 50;

        experience = 0;
        maxExperience = 100;

        gold = 0;
    }

    @Override
    public void reset() {
        level = 1;
        health = maxHealth = 100;
        spirit = maxSpirit = 50;

        experience = 0;
        maxExperience = 100;

        gold = 0;
    }
}
