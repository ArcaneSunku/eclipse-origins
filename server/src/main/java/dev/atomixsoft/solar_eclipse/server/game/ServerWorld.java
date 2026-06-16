package dev.atomixsoft.solar_eclipse.server.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.PooledEngine;
import dev.atomixsoft.solar_eclipse.server.database.repositories.AccountRepository;
import dev.atomixsoft.solar_eclipse.server.database.repositories.CharacterRepository;
import dev.atomixsoft.solar_eclipse.server.database.repositories.InventoryRepository;
import dev.atomixsoft.solar_eclipse.server.game.ecs.systems.MovementSystem;
import dev.atomixsoft.solar_eclipse.server.net.NetworkServer;
import dev.atomixsoft.solar_eclipse.server.net.services.*;

public class ServerWorld {

    private final NetworkServer m_Network;
    private final CharacterRepository m_Characters;
    private final Engine m_Engine;

    private final AuthenticationService m_AuthService;
    private final SessionService m_SessionService;
    private final PlayerService m_PlayerService;
    private final ClassService m_ClassService;
    private final ItemService m_ItemService;
    private final InventoryService m_InventoryService;
    private final ChatService m_ChatService;
    private final MapService m_MapService;

    public ServerWorld(NetworkServer network, AccountRepository accounts,
                       CharacterRepository characters, InventoryRepository inventory) {
        m_Network = network;
        m_Characters = characters;
        m_Engine = new PooledEngine(75, 256, 5, 256);

        m_AuthService = new AuthenticationService(accounts);
        m_SessionService = new SessionService();
        m_PlayerService = new PlayerService(m_Engine);
        m_ChatService = new ChatService(m_Network);
        m_MapService = new MapService();

        m_ClassService = new ClassService();
        m_ClassService.load("server/classes.ini");

        m_ItemService = new ItemService();
        m_ItemService.load("server/items.ini");

        m_InventoryService = new InventoryService(inventory, m_ItemService);

        initializeSystems();
    }

    public void update(float delta) {
        m_Engine.update(delta);
    }

    private void initializeSystems() {
        m_Engine.addSystem(new MovementSystem(m_MapService));
    }

    public AuthenticationService auth() {
        return m_AuthService;
    }

    public SessionService sessions() {
        return m_SessionService;
    }

    public PlayerService players() {
        return m_PlayerService;
    }

    public ItemService items() {
        return m_ItemService;
    }

    public InventoryService inventory() {
        return m_InventoryService;
    }

    public ClassService classes() {
        return m_ClassService;
    }

    public ChatService chat() {
        return m_ChatService;
    }

    public MapService maps() {
        return m_MapService;
    }

    public NetworkServer network() {
        return m_Network;
    }

    public CharacterRepository characters() {
        return m_Characters;
    }

    public Engine engine() {
        return m_Engine;
    }

}
