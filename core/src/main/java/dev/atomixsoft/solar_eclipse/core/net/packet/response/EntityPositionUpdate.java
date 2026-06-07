package dev.atomixsoft.solar_eclipse.core.net.packet.response;

import dev.atomixsoft.solar_eclipse.core.game.character.Direction;
import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public record EntityPositionUpdate(int entityId, int x, int y, byte direction, boolean moving, long serverTick) implements Packet {
}
