package dev.atomixsoft.solar_eclipse.core.net;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.codec.notification.ChatMessageBroadcastCodec;
import dev.atomixsoft.solar_eclipse.core.net.codec.notification.ShutdownNotificationCodec;
import dev.atomixsoft.solar_eclipse.core.net.codec.request.ChatMessageRequestCodec;
import dev.atomixsoft.solar_eclipse.core.net.codec.request.LoginRequestCodec;

import dev.atomixsoft.solar_eclipse.core.net.codec.request.MoveIntentCodec;
import dev.atomixsoft.solar_eclipse.core.net.codec.response.EntityPositionUpdateCodec;
import dev.atomixsoft.solar_eclipse.core.net.codec.response.LoginResponseCodec;
import dev.atomixsoft.solar_eclipse.core.net.codec.response.MapLoadCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.ChatMessageRequest;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.LoginRequest;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.MoveIntent;

import dev.atomixsoft.solar_eclipse.core.net.packet.response.EntityPositionUpdate;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.LoginResponse;

import dev.atomixsoft.solar_eclipse.core.net.packet.notification.ChatMessageBroadcast;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.ShutdownNotification;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.MapLoad;

import java.util.HashMap;
import java.util.Map;

public final class PacketRegistry {

    private static final Map<Integer, PacketCodec<? extends Packet>> ID_TO_CODEC = new HashMap<>();
    private static final Map<Class<? extends Packet>, PacketCodec<? extends Packet>> CLASS_TO_CODEC = new HashMap<>();

    private PacketRegistry() {}

    public static <T extends Packet> void Register(int id, Class<T> type, PacketCodec<T> codec) {

        if (ID_TO_CODEC.containsKey(id)) {
            throw new IllegalStateException("Duplicate packet id: " + id);
        }

        if (CLASS_TO_CODEC.containsKey(type)) {
            throw new IllegalStateException("Duplicate packet type: " + type.getName());
        }

        ID_TO_CODEC.put(id, codec);
        CLASS_TO_CODEC.put(type, codec);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Packet> PacketCodec<T> GetCodec(int id) {
        return (PacketCodec<T>) ID_TO_CODEC.get(id);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Packet> PacketCodec<T> GetCodec(Class<T> type) {
        return (PacketCodec<T>) CLASS_TO_CODEC.get(type);
    }

    public static int GetId(Class<? extends Packet> type) {
        for (var entry : CLASS_TO_CODEC.entrySet()) {
            if (entry.getKey() == type) {
                for (var idEntry : ID_TO_CODEC.entrySet()) {
                    if (idEntry.getValue() == entry.getValue()) {
                        return idEntry.getKey();
                    }
                }
            }
        }
        throw new IllegalStateException("Unregistered packet: " + type.getName());
    }

    public static void Initialize() {
        Register(100, LoginRequest.class, new LoginRequestCodec());
        Register(101, MoveIntent.class, new MoveIntentCodec());
        Register(102, ChatMessageRequest.class, new ChatMessageRequestCodec());

        Register(200, LoginResponse.class, new LoginResponseCodec());
        Register(201, EntityPositionUpdate.class, new EntityPositionUpdateCodec());
        Register(202, MapLoad.class, new MapLoadCodec());

        Register(300, ChatMessageBroadcast.class, new ChatMessageBroadcastCodec());
        Register(301, ShutdownNotification.class, new ShutdownNotificationCodec());
    }
}