package dev.atomixsoft.solar_eclipse.core.net.packet.impl;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public record EntityMovePacket(int id, float x, float y) implements Packet {
}
