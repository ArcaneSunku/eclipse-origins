package dev.atomixsoft.solar_eclipse.core.net.packet.response;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public record LoginResponse(boolean success, String message, int playerEntityId, int mapId) implements Packet {
}
