package dev.atomixsoft.solar_eclipse.core.net.packet.notification;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public record ShutdownNotification(String reason, int shutdownTime) implements Packet {
}
