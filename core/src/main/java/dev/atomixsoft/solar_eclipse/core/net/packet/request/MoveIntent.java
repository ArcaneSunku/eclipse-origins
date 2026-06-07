package dev.atomixsoft.solar_eclipse.core.net.packet.request;

import dev.atomixsoft.solar_eclipse.core.game.character.Direction;
import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public record MoveIntent(int dx, int dy, int sequence) implements Packet {
}
