package dev.atomixsoft.solar_eclipse.server;

import com.badlogic.ashley.core.Entity;
import dev.atomixsoft.solar_eclipse.core.game.character.CharacterData;
import dev.atomixsoft.solar_eclipse.core.game.character.Direction;
import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.EntityDespawn;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.EntitySpawn;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.ChatMessageRequest;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.LoginRequest;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.LogoutRequest;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.MoveIntent;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.EntityPositionUpdate;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.LoginResponse;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.MapLoad;
import dev.atomixsoft.solar_eclipse.server.game.ServerWorld;
import dev.atomixsoft.solar_eclipse.server.game.ecs.Components;
import dev.atomixsoft.solar_eclipse.server.game.ecs.components.MovementComponent;
import dev.atomixsoft.solar_eclipse.server.game.ecs.components.NameComponent;
import dev.atomixsoft.solar_eclipse.server.game.ecs.components.PositionComponent;
import dev.atomixsoft.solar_eclipse.server.net.NetworkServer;
import dev.atomixsoft.solar_eclipse.server.net.PacketQueue;
import dev.atomixsoft.solar_eclipse.server.net.QueuedPacket;

public class ServerThread implements Runnable {

    private final PacketQueue m_PacketQueue;
    private final ServerWorld m_World;
    private final Thread m_Thread;

    private volatile boolean m_Running;
    private long m_ServerTick;

    public ServerThread(PacketQueue packetQueue, NetworkServer network) {
        m_PacketQueue = packetQueue;
        m_World = new ServerWorld(network);
        m_Thread = new Thread(this, "Game_Loop");

        m_Running = false;
        m_ServerTick = 0;
    }

    public void start() {
        m_Running = true;
        m_Thread.start();
    }

    @Override
    public void run() {
        double accumulator = 0.0;
        double optimal = 1.0 / 20.0, maxFrameTime = 0.25;
        double currentTime = System.nanoTime() / 1e9;
        double newTime, frameTime;

        while(m_Running) {
            newTime = System.nanoTime() / 1e9;
            frameTime = newTime - currentTime;
            currentTime = newTime;

            if(frameTime > maxFrameTime)
                frameTime = maxFrameTime;

            accumulator += frameTime;
            while(accumulator >= optimal) {
                processPackets();
                m_World.update((float) optimal);
                sendSnapshots();

                m_ServerTick++;
                accumulator -= optimal;
            }

            sleep();
        }
    }

    public void stop() throws InterruptedException {
        m_Running = false;
        m_Thread.join(1L);
    }

    private void processPackets() {
        QueuedPacket queued;

        while((queued = m_PacketQueue.poll()) != null) {
            switch(queued.packet()) {
                case LoginRequest p -> {
                    if(!m_World.auth().validate(p.username(), p.password())) {
                        queued.channel().writeAndFlush(new LoginResponse(false, "Invalid login.", -1, -1));
                        break;
                    }

                    CharacterData data = new CharacterData();
                    data.name = p.username();
                    data.x = 2;
                    data.y = 3;
                    data.player = true;

                    Entity player = m_World.players().createPlayer(queued.channel(), data);
                    int entityId = m_World.players().getEntityId(player);

                    queued.channel().writeAndFlush(new LoginResponse(true, "Welcome " + p.username(), entityId, 0));

                    // Load a Test Map in the Server and Send to the Client
                    MapLoad mapLoad = m_World.maps().createMapLoad(0);

                    if(mapLoad != null)
                        queued.channel().writeAndFlush(mapLoad);

                    sendExistingEntitiesTo(queued);

                    EntitySpawn newPlayerSpawn = createEntitySpawn(player);
                    if(newPlayerSpawn != null)
                        m_World.network().broadcast(newPlayerSpawn);
                }

                case LogoutRequest p -> {
                    int entityId = m_World.players().removePlayer(queued.channel());

                    if(entityId != -1)
                        m_World.network().broadcast(new EntityDespawn(entityId));
                }

                case MoveIntent p -> {
                    Entity entity = m_World.players().getPlayer(queued.channel());

                    if(entity == null)
                        break;

                    MovementComponent movement;
                    if(Components.MOVE_INTENT.has(entity)) {
                        movement = Components.MOVE_INTENT.get(entity);
                    } else {
                        movement = m_World.getEngine().createComponent(MovementComponent.class);
                        entity.add(movement);
                    }

                    if(movement.moveCooldown > 0.0f || movement.moving)
                        break;

                    movement.dx = p.dx();
                    movement.dy = p.dy();
                    movement.moving = true;

                    if(p.dy() > 0) {
                        movement.direction = Direction.UP.asByte();
                    } else if (p.dy() < 0) {
                        movement.direction = Direction.DOWN.asByte();
                    } else if(p.dx() < 0) {
                        movement.direction = Direction.LEFT.asByte();
                    } else if(p.dx() > 0) {
                        movement.direction = Direction.RIGHT.asByte();
                    }
                }

                case ChatMessageRequest p -> {
                    Entity player = m_World.players().getPlayer(queued.channel());
                    if(player == null) break;

                    String sender = "Player";
                    if(Components.NAME.has(player))
                        sender = Components.NAME.get(player).name;

                    m_World.chat().broadcast(sender, p.message());
                }

                default -> {
                    System.out.println("Unhandled packet: " + queued.packet().getClass());
                }
            }
        }
    }

    private EntitySpawn createEntitySpawn(Entity entity) {
        int entityId = m_World.players().getEntityId(entity);

        PositionComponent position = Components.POSITION.get(entity);
        MovementComponent movement = Components.MOVE_INTENT.get(entity);
        NameComponent name = Components.NAME.get(entity);

        if(position == null || movement == null || name == null)
            return null;

        return new EntitySpawn(entityId, name.name, position.x, position.y, movement.direction);
    }

    private void sendExistingEntitiesTo(QueuedPacket queued) {
        for(Entity entity : m_World.players().getPlayers()) {
            EntitySpawn spawn = createEntitySpawn(entity);

            if(spawn != null)
                queued.channel().writeAndFlush(spawn);
        }
    }

    private void sendSnapshots() {
        for(Entity entity : m_World.players().getPlayers()) {
            int entityId = m_World.players().getEntityId(entity);

            PositionComponent position = Components.POSITION.get(entity);
            MovementComponent movement = Components.MOVE_INTENT.get(entity);

            if(position == null || movement == null)
                continue;

            m_World.network().broadcast(new EntityPositionUpdate(entityId, position.x, position.y,
                    movement.direction, movement.moving, m_ServerTick));
        }
    }

    private void sleep() {
        try {
            Thread.sleep(1L);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

}
