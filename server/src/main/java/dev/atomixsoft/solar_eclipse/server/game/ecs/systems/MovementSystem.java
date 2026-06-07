package dev.atomixsoft.solar_eclipse.server.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import dev.atomixsoft.solar_eclipse.core.game.Actuator;
import dev.atomixsoft.solar_eclipse.core.game.Constants;
import dev.atomixsoft.solar_eclipse.core.game.map.GameMap;
import dev.atomixsoft.solar_eclipse.core.game.map.Tile;
import dev.atomixsoft.solar_eclipse.server.game.ecs.Components;
import dev.atomixsoft.solar_eclipse.server.game.ecs.components.MovementComponent;
import dev.atomixsoft.solar_eclipse.server.game.ecs.components.PositionComponent;
import dev.atomixsoft.solar_eclipse.server.net.services.MapService;

public class MovementSystem extends IteratingSystem {

    private final MapService m_MapService;

    public MovementSystem(MapService mapService) {
        super(Family.all(PositionComponent.class, MovementComponent.class).get());
        m_MapService = mapService;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = Components.POSITION.get(entity);
        MovementComponent movement = Components.MOVE_INTENT.get(entity);

        if(movement.moveCooldown > 0.0f) {
            movement.moveCooldown -= deltaTime;

            if(movement.moveCooldown < 0.0f)
                movement.moveCooldown = 0.0f;

            return;
        }

        if(!movement.moving)
            return;

        int nextX = position.x + movement.dx;
        int nextY = position.y + movement.dy;

        GameMap map = m_MapService.getMap(0);

        if(map == null) {
            movement.reset();
            return;
        }

        Tile destination = Actuator.GetTileFromMap(map, 0, nextX, nextY);

        if(destination == null || destination.type != Constants.TILE_TYPE_WALKABLE) {
            movement.reset();
            return;
        }

        if(nextX < 0 || nextX >= map.width || nextY < 0 || nextY >= map.height) {
            movement.reset();
            return;
        }

        /*
            TODO:
                - Bounds Checking
                - Collision Checking
                - Tile Validation
                - Entity Collision
         */

        position.x = nextX;
        position.y = nextY;

        movement.moveCooldown = 0.25f;
        movement.reset();
    }
}
