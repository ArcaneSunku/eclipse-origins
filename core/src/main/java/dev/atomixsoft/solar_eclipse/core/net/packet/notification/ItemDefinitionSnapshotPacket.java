package dev.atomixsoft.solar_eclipse.core.net.packet.notification;

import dev.atomixsoft.solar_eclipse.core.net.data.ItemDefinitionData;
import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

import java.util.List;

public record ItemDefinitionSnapshotPacket(List<ItemDefinitionData> items) implements Packet {
}
