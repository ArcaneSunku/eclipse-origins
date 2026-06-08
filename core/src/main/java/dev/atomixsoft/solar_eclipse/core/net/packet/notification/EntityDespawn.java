package dev.atomixsoft.solar_eclipse.core.net.packet.notification;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public record EntityDespawn(int entityId) implements Packet {
}
