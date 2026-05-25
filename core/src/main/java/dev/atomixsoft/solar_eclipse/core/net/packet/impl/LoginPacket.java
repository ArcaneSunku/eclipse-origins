package dev.atomixsoft.solar_eclipse.core.net.packet.impl;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public record LoginPacket(String username, String password) implements Packet {
}
