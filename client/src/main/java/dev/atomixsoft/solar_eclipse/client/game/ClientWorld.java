package dev.atomixsoft.solar_eclipse.client.game;

import dev.atomixsoft.solar_eclipse.client.graphics.GameRenderer;
import dev.atomixsoft.solar_eclipse.client.net.ClientSession;
import dev.atomixsoft.solar_eclipse.core.game.Actuator;
import dev.atomixsoft.solar_eclipse.core.game.character.CharacterData;
import dev.atomixsoft.solar_eclipse.core.game.character.Direction;
import dev.atomixsoft.solar_eclipse.core.game.map.GameMap;
import dev.atomixsoft.solar_eclipse.core.game.map.Tile;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.EntityDespawn;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.EntitySpawn;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.InventorySnapshotPacket;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.EntityPositionUpdate;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.MapLoad;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.PlayerStatsSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientWorld {

    private final GameRenderer m_Renderer;
    private final ClientPlayerStats m_PlayerStats;
    private final ClientInventory m_Inventory;
    private final ClientItemDefinitions m_Items;
    private final Map<Integer, CharacterData> m_EntitiesById;

    private CharacterData m_Player;
    private int m_LocalEntityId;

    public ClientWorld(GameRenderer renderer) {
        m_Renderer = renderer;
        m_PlayerStats = new ClientPlayerStats();
        m_Inventory = new ClientInventory();
        m_Items = new ClientItemDefinitions();
        m_EntitiesById = new HashMap<>();

        m_Player = null;
        m_LocalEntityId = -1;
    }

    public void reset() {
        m_EntitiesById.clear();

        m_Player = null;
        resetPlayerStats();
        m_Items.applySnapshot(List.of());
        m_LocalEntityId = -1;

        m_Renderer.setMap(null);
    }

    public void resetPlayerStats() {
        m_PlayerStats.health = 100;
        m_PlayerStats.maxHealth = 100;

        m_PlayerStats.spirit = 35;
        m_PlayerStats.maxSpirit = 50;

        m_PlayerStats.experience = 20;
        m_PlayerStats.maxExperience = 100;

        m_PlayerStats.gold = 1337;
        m_PlayerStats.ping = 0;
    }

    public void applyPositionUpdate(EntityPositionUpdate packet) {
        if(m_Renderer.getMap() == null)
            return;

        CharacterData entity = m_EntitiesById.get(packet.entityId());
        if(entity == null)
            return;

        entity.x = packet.x();
        entity.y = packet.y();
        entity.facing = Direction.Get(packet.direction());
        entity.moving = false;
    }

    public void applyStatsSnapshot(PlayerStatsSnapshot packet) {
        m_PlayerStats.health = packet.health();
        m_PlayerStats.maxHealth = packet.maxHealth();

        m_PlayerStats.spirit = packet.spirit();
        m_PlayerStats.maxSpirit = packet.maxSpirit();

        m_PlayerStats.experience = packet.experience();
        m_PlayerStats.maxExperience = packet.maxExperience();

        m_PlayerStats.gold = packet.gold();
    }

    public void applyMapLoad(MapLoad packet) {
        GameMap map = new GameMap(0, 0, packet.width(), packet.height());
        map.id = (byte) packet.mapdId();

        Tile baseTile = new Tile();
        baseTile.textureId = packet.baseTexId();
        baseTile.textureX = packet.baseTexX();
        baseTile.textureY = packet.baseTexY();
        baseTile.type = packet.baseTileType();
        baseTile.roof = false;

        Actuator.FillMapLayer(map, baseTile, 0);

        m_EntitiesById.clear();
        m_Player = null;
        m_LocalEntityId = -1;

        m_Renderer.setMap(map);
    }

    public void applyEntityDespawn(EntityDespawn packet) {
        CharacterData entity = m_EntitiesById.remove(packet.entityId());

        if(entity == null)
            return;

        if(m_Renderer.getMap() != null)
            m_Renderer.getMap().MapCharacters.remove(entity);

        if (entity == m_Player) {
            m_Player = null;
            m_LocalEntityId = -1;
        }
    }

    public void applyEntitySpawn(EntitySpawn packet) {
        if(m_Renderer.getMap() == null)
            return;

        CharacterData entity = m_EntitiesById.get(packet.entityId());

        if(entity == null) {
            entity = new CharacterData();

            Actuator.AddCharacterToMap(m_Renderer.getMap(), entity, packet.tileX(), packet.tileY());
            m_EntitiesById.put(packet.entityId(), entity);
        }

        entity.name = packet.name();
        entity.textureId = packet.textureId();
        entity.player = true;
        entity.x = packet.tileX();
        entity.y = packet.tileY();
        entity.facing = Direction.Get(packet.direction());
        entity.moving = false;

        if(packet.entityId() == ClientSession.GetLocalEntityId()) {
            m_Player = entity;
            m_LocalEntityId = packet.entityId();
        }
    }

    public ClientInventory getInventory() {
        return m_Inventory;
    }

    public ClientItemDefinitions getItems() {
        return m_Items;
    }

    public ClientPlayerStats getPlayerStats() {
        return m_PlayerStats;
    }

    public CharacterData getPlayer() {
        return m_Player;
    }

    public int getLocalEntityId() {
        return m_LocalEntityId;
    }

    public GameMap getMap() {
        return m_Renderer.getMap();
    }

}
