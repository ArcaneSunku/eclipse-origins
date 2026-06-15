package dev.atomixsoft.solar_eclipse.server.net.services;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.IntMap;
import dev.atomixsoft.solar_eclipse.core.game.character.CharacterData;
import dev.atomixsoft.solar_eclipse.server.game.ecs.Components;
import dev.atomixsoft.solar_eclipse.server.game.ecs.components.*;
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
        if(data == null)
            return null;

        Entity entity = m_Engine.createEntity();

        AccountComponent account = m_Engine.createComponent(AccountComponent.class);
        account.accountId = data.accountId;

        IdentityComponent identity = m_Engine.createComponent(IdentityComponent.class);
        identity.entityId = m_Ids.generateId();

        NameComponent name = m_Engine.createComponent(NameComponent.class);
        name.name = data.name;

        SpriteComponent sprite = m_Engine.createComponent(SpriteComponent.class);
        sprite.textureId = data.textureId;

        PositionComponent position = m_Engine.createComponent(PositionComponent.class);
        position.x = data.x;
        position.y = data.y;
        position.mapId = data.mapId;

        MovementComponent movement = m_Engine.createComponent(MovementComponent.class);
        PlayerComponent player = m_Engine.createComponent(PlayerComponent.class);

        PersistenceComponent persistence = m_Engine.createComponent(PersistenceComponent.class);
        persistence.accountId = data.accountId;
        persistence.characterId = data.characterId;

        StatsComponent stats = m_Engine.createComponent(StatsComponent.class);
        stats.level = data.level;
        stats.health = data.health;
        stats.maxHealth = data.maxHealth;
        stats.spirit = data.spirit;
        stats.maxSpirit = data.maxSpirit;
        stats.experience = data.experience;
        stats.maxExperience = data.maxExperience;
        stats.gold = data.gold;

        entity.add(account);
        entity.add(identity);
        entity.add(name);
        entity.add(sprite);

        entity.add(persistence);
        entity.add(stats);

        entity.add(position);
        entity.add(movement);
        entity.add(player);

        m_Engine.addEntity(entity);
        m_EntitiesById.put(identity.entityId, entity);
        m_PlayersByChannel.put(channel, entity);

        return entity;
    }

    public int removePlayer(Channel channel) {
        Entity entity = m_PlayersByChannel.remove(channel);
        if(entity == null)
            return -1;

        int entityId = getEntityId(entity);

        if(entityId != -1)
            m_EntitiesById.remove(entityId);

        m_Engine.removeEntity(entity);
        return entityId;
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
