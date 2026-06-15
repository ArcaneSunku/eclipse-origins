package dev.atomixsoft.solar_eclipse.core.net.packet.response;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

import java.util.List;

public record CharacterListResponse(List<CharacterSummaryPacket> characters) implements Packet {
}
