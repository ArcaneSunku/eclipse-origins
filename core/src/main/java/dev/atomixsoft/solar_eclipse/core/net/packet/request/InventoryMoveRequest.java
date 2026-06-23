package dev.atomixsoft.solar_eclipse.core.net.packet.request;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public record InventoryMoveRequest(int fromSlot, int toSlot) implements Packet {
}
