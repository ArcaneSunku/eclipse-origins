package dev.atomixsoft.solar_eclipse.core.net.codec.notification;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.data.InventorySlotData;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.InventorySnapshotPacket;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class InventorySnapshotCodec implements PacketCodec<InventorySnapshotPacket> {
    @Override
    public void encode(InventorySnapshotPacket packet, ByteBuf out) {
        out.writeInt(packet.slots().size());

        for(InventorySlotData slot : packet.slots()) {
            out.writeInt(slot.slot());
            out.writeInt(slot.itemId());
            out.writeInt(slot.amount());
        }
    }

    @Override
    public InventorySnapshotPacket decode(ByteBuf in) {
        int count = in.readInt();

        List<InventorySlotData> slots = new ArrayList<>();
        for(int i = 0; i  < count; i++) {
            int slot = in.readInt();
            int itemId = in.readInt();
            int amount = in.readInt();

            slots.add(new InventorySlotData(slot, itemId, amount));
        }

        return new InventorySnapshotPacket(slots);
    }
}
