package dev.atomixsoft.solar_eclipse.server.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.PooledEngine;
import dev.atomixsoft.solar_eclipse.server.game.ecs.systems.MovementSystem;
import dev.atomixsoft.solar_eclipse.server.net.NetworkServer;
import dev.atomixsoft.solar_eclipse.server.net.services.AuthenticationService;
import dev.atomixsoft.solar_eclipse.server.net.services.ChatService;
import dev.atomixsoft.solar_eclipse.server.net.services.MapService;
import dev.atomixsoft.solar_eclipse.server.net.services.PlayerService;

public class ServerWorld {

    private final NetworkServer m_Network;
    private final Engine m_Engine;

    private final AuthenticationService m_AuthService;
    private final PlayerService m_PlayerService;
    private final ChatService m_ChatService;
    private final MapService m_MapService;

    public ServerWorld(NetworkServer network) {
        m_Network = network;
        m_Engine = new PooledEngine(75, 256, 5, 256);

        m_AuthService = new AuthenticationService();
        m_PlayerService = new PlayerService(m_Engine);
        m_ChatService = new ChatService(m_Network);
        m_MapService = new MapService();

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

    public PlayerService players() {
        return m_PlayerService;
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

    public Engine getEngine() {
        return m_Engine;
    }

}
