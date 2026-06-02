package dev.atomixsoft.solar_eclipse.core.net.packet.response;

import dev.atomixsoft.solar_eclipse.core.game.character.Direction;
import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public record EntityPositionUpdate(int entityId, float x, float y, Direction direction, boolean moving, double serverTick) implements Packet {
}
