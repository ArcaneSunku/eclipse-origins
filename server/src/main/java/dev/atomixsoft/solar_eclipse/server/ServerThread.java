package dev.atomixsoft.solar_eclipse.server;

import com.badlogic.ashley.core.Entity;
import dev.atomixsoft.solar_eclipse.core.game.Constants;
import dev.atomixsoft.solar_eclipse.core.game.character.Direction;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.EntityDespawn;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.EntitySpawn;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.InventorySnapshotPacket;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.ItemDefinitionSnapshotPacket;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.*;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.*;
import dev.atomixsoft.solar_eclipse.server.database.records.CharacterRecord;
import dev.atomixsoft.solar_eclipse.server.database.records.CharacterSummary;
import dev.atomixsoft.solar_eclipse.server.database.repositories.AccountRepository;
import dev.atomixsoft.solar_eclipse.server.database.repositories.CharacterRepository;
import dev.atomixsoft.solar_eclipse.server.database.repositories.InventoryRepository;
import dev.atomixsoft.solar_eclipse.server.game.ServerWorld;
import dev.atomixsoft.solar_eclipse.server.game.classes.ClassDefinition;
import dev.atomixsoft.solar_eclipse.server.game.ecs.Components;
import dev.atomixsoft.solar_eclipse.server.game.ecs.components.*;
import dev.atomixsoft.solar_eclipse.server.net.NetworkServer;
import dev.atomixsoft.solar_eclipse.server.net.PacketQueue;
import dev.atomixsoft.solar_eclipse.server.net.QueuedPacket;
import dev.atomixsoft.solar_eclipse.server.net.services.SessionService;
import dev.atomixsoft.solar_eclipse.server.net.services.records.LoginResultRec;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

public class ServerThread implements Runnable {

    private final PacketQueue m_PacketQueue;
    private final ServerWorld m_World;
    private final Thread m_Thread;

    private volatile boolean m_Running;
    private long m_ServerTick;

    public ServerThread(PacketQueue packetQueue, NetworkServer network,
                        AccountRepository accounts, CharacterRepository characters,
                        InventoryRepository inventory) {
        m_PacketQueue = packetQueue;
        m_World = new ServerWorld(network, accounts, characters, inventory);
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
                    handleAuthResult(queued, m_World.auth().login(p.username(), p.password()));
                }

                case RegisterRequest p -> {
                    handleAuthResult(queued, m_World.auth().register(p.username(), p.password()));
                }

                case LogoutRequest p -> {
                    Entity entity = m_World.players().getPlayer(queued.channel());
                    if(entity != null)
                        savePlayer(entity);

                    int entityId = m_World.players().removePlayer(queued.channel());

                    if(entityId != -1)
                        m_World.network().broadcast(new EntityDespawn(entityId));

                    m_World.sessions().removeSession(queued.channel());
                }

                case CharacterListRequest p -> {
                    sendCharacterList(queued.channel());
                }

                case CharacterSelectReq p -> {
                    handleCharacterSelect(queued, p);
                }

                case CreateCharacterRequest p -> {
                    handleCharacterCreate(queued, p);
                }

                case MoveIntent p -> {
                    Entity entity = m_World.players().getPlayer(queued.channel());

                    if(entity == null)
                        break;

                    MovementComponent movement;
                    if(Components.MOVE_INTENT.has(entity)) {
                        movement = Components.MOVE_INTENT.get(entity);
                    } else {
                        movement = m_World.engine().createComponent(MovementComponent.class);
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

                case InventoryMoveRequest p -> {
                    handleInventoryMove(queued, p);
                }

                default -> {
                    System.out.println("Unhandled packet: " + queued.packet().getClass());
                }
            }
        }
    }

    private void handleAuthResult(QueuedPacket queued, LoginResultRec login) {
        if(!login.success()) {
            queued.channel().writeAndFlush(new LoginResponse(false, login.message(), "", -1, -1));
            return;
        }

        m_World.sessions().createSession(queued.channel(), login.accountId());
        queued.channel().writeAndFlush(new LoginResponse(true, login.message(), login.username(), -1, -1));

        sendCharacterList(queued.channel());
    }

    private void sendStatsTo(Channel channel, Entity entity) {
        if(!Components.STATS.has(entity))
            return;

        StatsComponent stats = Components.STATS.get(entity);
        channel.writeAndFlush(new PlayerStatsSnapshot(stats.level, stats.health, stats.maxHealth,
                stats.spirit, stats.maxSpirit, stats.experience, stats.maxExperience, stats.gold));
    }

    private void sendCharacterList(Channel channel) {
        Integer accountId = m_World.sessions().getAccountId(channel);
        if(accountId == null)
            return;

        List<CharacterSummary> summaries = m_World.characters().getCharacterSummaries(accountId);
        List<CharacterSummaryPacket> packets = new ArrayList<>();

        for(CharacterSummary summary : summaries)
            packets.add(new CharacterSummaryPacket(summary.slot(), summary.name(), summary.level(), summary.spriteId()));

        channel.writeAndFlush(new CharacterListResponse(packets));
    }

    private void handleCharacterSelect(QueuedPacket queued, CharacterSelectReq request) {
        Integer accountId = m_World.sessions().getAccountId(queued.channel());
        if(accountId == null)
            return;

        if(m_World.players().getPlayer(queued.channel()) != null)
            return;

        CharacterRecord character = m_World.characters().findByAccountAndSlot(accountId, request.slot());
        if(character == null)
            return;

        m_World.sessions().selectCharacter(queued.channel(), character.id());
        Entity player = m_World.players().createPlayer(queued.channel(), m_World.characters().toCharacterData(character));

        int entityId = m_World.players().getEntityId(player);
        queued.channel().writeAndFlush(new LoginResponse(true, "Entering world...", character.name(), entityId, character.mapId()));

        MapLoad mapLoad = m_World.maps().createMapLoad(character.mapId());
        if(mapLoad != null)
            queued.channel().writeAndFlush(mapLoad);

        sendStatsTo(queued.channel(), player);
        sendExistingEntitiesTo(queued);

        EntitySpawn spawn = createEntitySpawn(player);
        if(spawn != null)
            m_World.network().broadcast(spawn);

        queued.channel().writeAndFlush(new ItemDefinitionSnapshotPacket(m_World.items().createSnapshot()));
        queued.channel().writeAndFlush(new InventorySnapshotPacket(m_World.inventory().createSnapshot(character.id())));
    }

    private void handleCharacterCreate(QueuedPacket queued, CreateCharacterRequest request) {
        Integer accountId = m_World.sessions().getAccountId(queued.channel());
        if(accountId == null)
            return;

        if(request.slot() < 0 || request.slot() >= 4)
            return;

        CharacterRecord existing = m_World.characters().findByAccountAndSlot(accountId, request.slot());

        if(existing != null) {
            sendCharacterList(queued.channel());
            return;
        }

        ClassDefinition clazz = m_World.classes().get(request.classId());

        if(clazz == null) {
            sendCharacterList(queued.channel());
            return;
        }

        boolean validSprite;

        if(request.sex() == Constants.SEX_MALE)
            validSprite = clazz.maleSprites().contains(request.spriteId());
        else if(request.sex() == Constants.SEX_FEMALE)
            validSprite = clazz.femaleSprites().contains(request.spriteId());
        else
            validSprite = false;

        if(!validSprite) {
            sendCharacterList(queued.channel());
            return;
        }

        CharacterRecord created = m_World.characters().createCharacter(accountId, request.slot(), request.name(), clazz, request.sex(),  request.spriteId());
        m_World.inventory().createStartingInventory(created.id(), clazz.startingItems());

        sendCharacterList(queued.channel());
    }

    private void handleInventoryMove(QueuedPacket queued, InventoryMoveRequest packet) {
        if(!m_World.sessions().hasSession(queued.channel()))
            return;

        int characterId = m_World.sessions().getCharacterId(queued.channel());

        m_World.inventory().moveItem(characterId, packet.fromSlot(), packet.toSlot());
        queued.channel().writeAndFlush(new InventorySnapshotPacket(m_World.inventory().createSnapshot(characterId)));
    }

    private EntitySpawn createEntitySpawn(Entity entity) {
        int entityId = m_World.players().getEntityId(entity);

        PositionComponent position = Components.POSITION.get(entity);
        MovementComponent movement = Components.MOVE_INTENT.get(entity);
        NameComponent name = Components.NAME.get(entity);
        SpriteComponent sprite = Components.SPRITE.get(entity);

        if(position == null || movement == null || name == null || sprite == null)
            return null;

        return new EntitySpawn(entityId, name.name, sprite.textureId, position.x, position.y, movement.direction);
    }

    private void sendExistingEntitiesTo(QueuedPacket queued) {
        for(Entity entity : m_World.players().getPlayers()) {
            EntitySpawn spawn = createEntitySpawn(entity);

            if(spawn != null)
                queued.channel().writeAndFlush(spawn);
        }
    }

    private void savePlayer(Entity entity) {
        if(entity == null)
            return;

        PersistenceComponent persistence = Components.PERSISTENCE.get(entity);
        PositionComponent position = Components.POSITION.get(entity);
        StatsComponent stats  = Components.STATS.get(entity);

        if(persistence == null || position == null || stats == null)
            return;

        m_World.characters().saveState(persistence.characterId,
                position.mapId, position.x, position.y,
                stats.level,
                stats.health, stats.maxHealth,
                stats.spirit, stats.maxSpirit,
                stats.experience, stats.maxExperience, stats.gold);
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
