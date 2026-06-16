package dev.atomixsoft.solar_eclipse.core.net.packet.notification;

import dev.atomixsoft.solar_eclipse.core.net.data.InventorySlotData;
import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

import java.util.List;

public record InventorySnapshotPacket(List<InventorySlotData> slots) implements Packet {
}
