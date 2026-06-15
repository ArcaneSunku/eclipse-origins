package dev.atomixsoft.solar_eclipse.core.net.packet.notification;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public record EntitySpawn(int entityId, String name, int textureId, int tileX, int tileY, byte direction) implements Packet {
}
