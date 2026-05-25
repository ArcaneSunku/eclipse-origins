package dev.atomixsoft.solar_eclipse.core.net.packet.impl;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public record ShutdownPacket(boolean forced) implements Packet {
}
