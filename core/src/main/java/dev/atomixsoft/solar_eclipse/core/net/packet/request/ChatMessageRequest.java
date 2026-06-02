package dev.atomixsoft.solar_eclipse.core.net.packet.request;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public record ChatMessageRequest(String username, String message) implements Packet {
}
