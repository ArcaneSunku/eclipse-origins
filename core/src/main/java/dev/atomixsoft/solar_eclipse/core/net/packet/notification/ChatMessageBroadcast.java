package dev.atomixsoft.solar_eclipse.core.net.packet.notification;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public record ChatMessageBroadcast(String username, String message, int timestamp) implements Packet {
}
