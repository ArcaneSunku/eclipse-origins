package dev.atomixsoft.solar_eclipse.core.net.packet;

import dev.atomixsoft.solar_eclipse.core.net.packet.impl.ChatMessagePacket;
import dev.atomixsoft.solar_eclipse.core.net.packet.impl.EntityMovePacket;
import dev.atomixsoft.solar_eclipse.core.net.packet.impl.LoginPacket;
import dev.atomixsoft.solar_eclipse.core.net.packet.impl.ShutdownPacket;

import java.util.HashMap;
import java.util.Map;

public class PacketRegistry {

    private static final Map<Integer, Class<? extends Packet>> ID_TO_PACKET = new HashMap<>();
    private static final Map<Class<? extends Packet>, Integer> PACKET_TO_ID = new HashMap<>();

    private PacketRegistry() {}

    public static void Initialize() {
        // Client -> Server
        Register(1, LoginPacket.class);

        // Server -> Client
        Register(2, EntityMovePacket.class);

        // Client <-> Server
        Register(3, ChatMessagePacket.class);
        Register(0, ShutdownPacket.class);
    }

    public static void Register(int id, Class<? extends Packet> packetClass) {
        if(ID_TO_PACKET.containsKey(id))
            throw new IllegalStateException("Duplicate packet Id: " + id);

        if(PACKET_TO_ID.containsKey(packetClass))
            throw new IllegalStateException("Duplicate packet class: " + packetClass.getName());

        ID_TO_PACKET.put(id, packetClass);
        PACKET_TO_ID.put(packetClass, id);
    }

    public static int GetId(Packet packet) {
        Integer id = PACKET_TO_ID.get(packet.getClass());

        if(id == null)
            throw new IllegalStateException("Unregistered packet type: " + packet.getClass().getName());

        return id;
    }

    public static Class<? extends Packet> GetPacket(int id) {
        Class<? extends Packet> packet = ID_TO_PACKET.get(id);

        if(packet == null)
            throw new IllegalStateException("Unknown packet Id: " + id);

        return packet;
    }

}
