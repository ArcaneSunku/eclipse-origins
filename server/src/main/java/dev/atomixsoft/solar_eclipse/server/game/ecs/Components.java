package dev.atomixsoft.solar_eclipse.server.game.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import dev.atomixsoft.solar_eclipse.server.game.ecs.components.*;

public class Components {

    public static final ComponentMapper<IdentityComponent> IDENTITY = ComponentMapper.getFor(IdentityComponent.class);
    public static final ComponentMapper<PlayerComponent> PLAYER = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<NameComponent> NAME = ComponentMapper.getFor(NameComponent.class);

    public static final ComponentMapper<DirectionComponent> DIRECTION = ComponentMapper.getFor(DirectionComponent.class);
    public static final ComponentMapper<PositionComponent> POSITION = ComponentMapper.getFor(PositionComponent.class);
    public static final ComponentMapper<MovementComponent> MOVE_INTENT = ComponentMapper.getFor(MovementComponent.class);

    public static final ComponentMapper<PersistenceComponent> PERSISTENCE = ComponentMapper.getFor(PersistenceComponent.class);

}
