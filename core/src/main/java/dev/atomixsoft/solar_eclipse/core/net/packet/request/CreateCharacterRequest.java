package dev.atomixsoft.solar_eclipse.core.net.packet.request;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public record CreateCharacterRequest(int slot, String name, int classId, byte sex, int spriteId) implements Packet {
}
