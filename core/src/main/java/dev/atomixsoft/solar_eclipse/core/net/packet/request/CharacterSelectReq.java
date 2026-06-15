package dev.atomixsoft.solar_eclipse.core.net.packet.request;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public record CharacterSelectReq(int slot) implements Packet {
}
