package dev.atomixsoft.solar_eclipse.server.net.services;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.IntMap;
import dev.atomixsoft.solar_eclipse.core.game.character.CharacterData;
import dev.atomixsoft.solar_eclipse.server.game.ecs.Components;
import dev.atomixsoft.solar_eclipse.server.game.ecs.components.IdentityComponent;
import dev.atomixsoft.solar_eclipse.server.game.ecs.components.MovementComponent;
import dev.atomixsoft.solar_eclipse.server.game.ecs.components.PlayerComponent;
import dev.atomixsoft.solar_eclipse.server.game.ecs.components.PositionComponent;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class PlayerService {

    private final IntMap<Entity> m_EntitiesById;
    private final Map<Channel, Entity> m_PlayersByChannel;

    private final Engine m_Engine;
    private final EntityIdGenerator m_Ids;

    public PlayerService(Engine engine) {
        m_Engine = engine;
        m_Ids = new EntityIdGenerator();

        m_EntitiesById = new IntMap<>();
        m_PlayersByChannel = new HashMap<>();
    }

    public Entity createPlayer(Channel channel, CharacterData data) {
        Entity entity = m_Engine.createEntity();

        IdentityComponent identity = m_Engine.createComponent(IdentityComponent.class);
        identity.entityId = m_Ids.generateId();

        PositionComponent position = m_Engine.createComponent(PositionComponent.class);
        position.x = data.x;
        position.y = data.y;

        MovementComponent movement = m_Engine.createComponent(MovementComponent.class);
        PlayerComponent player = m_Engine.createComponent(PlayerComponent.class);

        entity.add(identity);
        entity.add(position);
        entity.add(movement);
        entity.add(player);

        m_Engine.addEntity(entity);
        m_EntitiesById.put(identity.entityId, entity);
        m_PlayersByChannel.put(channel, entity);

        return entity;
    }

    public void removePlayer(Channel channel) {
        Entity entity = m_PlayersByChannel.remove(channel);
        if(entity == null)
            return;

        IdentityComponent identity = Components.IDENTITY.get(entity);
        if(identity != null)
            m_EntitiesById.remove(identity.entityId);

        m_Engine.removeEntity(entity);
    }

    public Iterable<Entity> getPlayers() {
        return m_PlayersByChannel.values();
    }

    public Entity getPlayer(Channel channel) {
        return m_PlayersByChannel.get(channel);
    }

    public Entity getPlayer(int entityId) {
        return m_EntitiesById.get(entityId);
    }

    public int getEntityId(Entity entity) {
        if(!Components.IDENTITY.has(entity))
            return -1;

        IdentityComponent identity = Components.IDENTITY.get(entity);
        return identity.entityId;
    }

}
